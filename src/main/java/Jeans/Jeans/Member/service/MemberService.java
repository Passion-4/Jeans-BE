package Jeans.Jeans.Member.service;

import Jeans.Jeans.Member.domain.Member;
import Jeans.Jeans.Member.domain.RefreshToken;
import Jeans.Jeans.Member.dto.LoginResponseDto;
import Jeans.Jeans.Member.repository.MemberRepository;
import Jeans.Jeans.global.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;
    private final RefreshTokenService refreshTokenService;

    @Value("${spring.jwt.secret-key}")
    private String accessKey;

    @Value("${spring.jwt.refresh-key}")
    private String refreshKey;

    // Access 토큰 만료 시간을 1시간으로 설정
    private Long AccessExpireTimeMs = 1000 * 60 * 60L;

    // Refresh 토큰 만료 시간을 7일로 설정
    private Long RefreshExpireTimeMs = 7 * 24 * 1000 * 60 * 60L;

    public String signUp(String name, String birthday, String phone, String password){
        if(existsByPhone(phone)) throw new RuntimeException(phone + "은 이미 존재하는 전화번호입니다.");

        memberRepository.save(
                Member.builder()
                        .name(name)
                        .birthday(birthday)
                        .phone(phone)
                        .password(encoder.encode(password))
                        .profileUrl(null)
                        .build()
        );
        return "회원가입이 완료되었습니다.";
    }

    // 로그인
    public LoginResponseDto login(String phone, String password){
        // 존재하지 않는 아이디로 로그인을 시도한 경우를 캐치
        Member member = findMemberByPhone(phone);

        // 존재하는 아이디를 입력했지만 잘못된 비밀번호를 입력한 경우를 캐치
        if(!encoder.matches(password, member.getPassword())) throw new RuntimeException("잘못된 비밀번호를 입력했습니다.");

        // 로그인 성공 -> 토큰 생성
        String accessToken = JwtUtil.createAccessToken(member.getPhone(), accessKey, AccessExpireTimeMs);
        String refreshToken = JwtUtil.createRefreshToken(member.getPhone(), refreshKey, RefreshExpireTimeMs);

        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setMemberId(member.getMemberId());
        refreshTokenEntity.setValue(refreshToken);
        refreshTokenService.addRefreshToken(refreshTokenEntity);

        return LoginResponseDto.builder()
                .memberId(member.getMemberId())
                .phone(member.getPhone())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // AccessToken 재발급
    public LoginResponseDto refresh(String refreshTokenValue){
        // 해당 RefreshToken이 유효한지 DB에서 탐색
        RefreshToken refreshToken = refreshTokenService.findRefreshToken(refreshTokenValue);

        // RefreshToken에 담긴 아이디 값 가져오기
        Claims claims = JwtUtil.parseRefreshToken(refreshToken.getValue(), refreshKey);
        String phone = claims.get("userId").toString();
        System.out.println("RefreshToken에 담긴 아이디 : " + phone);

        // 가져온 아이디에 해당하는 member가 존재하는지 확인
        Member member = findMemberByPhone(phone);

        // 새 AccessToken 생성
        String accessToken = JwtUtil.createAccessToken(member.getPhone(), accessKey, AccessExpireTimeMs);

        // 새 AccessToken과 기존 RefreshToken을 DTO에 담아 리턴
        return LoginResponseDto
                .builder()
                .memberId(member.getMemberId())
                .phone(member.getPhone())
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .build();
    }

    // 회원탈퇴
    public String delete(Authentication authentication){
        Member member = getLoginMember();
        memberRepository.delete(member);
        return "회원탈퇴가 완료되었습니다.";
    }

    // 회원 가입 시 입력한 전화번호를 가진 member 존재 여부 확인
    @Transactional(readOnly = true)
    public boolean existsByPhone(String phone){
        return memberRepository.existsByPhone(phone);
    }

    // 아이디로 member 찾기
    @Transactional(readOnly = true)
    public Member findMemberByPhone(String phone){
        return memberRepository.findByPhone(phone)
                .orElseThrow(() -> new EntityNotFoundException("아이디가 " + phone + "인 회원이 존재하지 않습니다."));
    }

    // 현재 로그인한 member 불러오기
    public Member getLoginMember(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phone = authentication.getName();
        return memberRepository.findByPhone(phone)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "인증된 회원 정보가 없습니다."));
    }
}
