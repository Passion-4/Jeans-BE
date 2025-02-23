package Jeans.Jeans.Member.service;

import Jeans.Jeans.BasicEdit.domain.BasicEdit;
import Jeans.Jeans.BasicEdit.respository.BasicEditRepository;
import Jeans.Jeans.Member.domain.Member;
import Jeans.Jeans.Member.domain.RefreshToken;
import Jeans.Jeans.Member.dto.BasicEditRequestDto;
import Jeans.Jeans.Member.dto.FollowTargetDto;
import Jeans.Jeans.Member.dto.LoginResponseDto;
import Jeans.Jeans.Member.dto.PasswordChangeDto;
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

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;
    private final RefreshTokenService refreshTokenService;
    private final BasicEditRepository basicEditRepository;

    @Value("${spring.jwt.secret-key}")
    private String accessKey;

    @Value("${spring.jwt.refresh-key}")
    private String refreshKey;

    // Access 토큰 만료 시간을 1시간으로 설정
    private Long AccessExpireTimeMs = 1000 * 60 * 60L;

    // Refresh 토큰 만료 시간을 7일로 설정
    private Long RefreshExpireTimeMs = 7 * 24 * 1000 * 60 * 60L;

    public String signUp(String name, String birthday, String phone, String password, Long voiceType){
        if(existsByPhone(phone)) throw new RuntimeException(phone + "은 이미 존재하는 전화번호입니다.");

        Member member = new Member(name, birthday, phone, encoder.encode(password), voiceType, null);
        memberRepository.save(member);
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

        int birthYear = Integer.parseInt(member.getBirthday().substring(0, 2));
        birthYear += (birthYear <= LocalDate.now().getYear() % 100) ? 2000 : 1900;
        int currentYear = LocalDate.now().getYear();
        Integer age = currentYear - birthYear + 1;

        Boolean exists = false;
        if (member.getProfileUrl() != null){
            exists = true;
        }
        return new LoginResponseDto(member.getMemberId(), age, exists, member.getPhone(), accessToken, refreshToken);
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

    // 기본 보정 설정
    public void createBasicEdit(Member member, BasicEditRequestDto requestDto){
        BasicEdit basicEdit = new BasicEdit(member, requestDto.getEdit1(), requestDto.getEdit2(), requestDto.getEdit3());
        basicEditRepository.save(basicEdit);
    }

    // 기본 보정 설정 변경
    public void updateBasicEdit(Member member, BasicEditRequestDto requestDto){
        BasicEdit basicEdit = basicEditRepository.findByMember(member);
        basicEdit.updateBasicEdit(requestDto.getEdit1(), requestDto.getEdit2(), requestDto.getEdit3());
        basicEditRepository.save(basicEdit);
    }

    // 팔로우할 회원 검색
    public FollowTargetDto getFollowTarget(String name, String phone){
        Member member = memberRepository.findByNameAndPhone(name, phone)
                .orElseThrow(() -> new EntityNotFoundException("해당 이름과 전화번호에 해당하는 회원이 존재하지 않습니다."));
        return new FollowTargetDto(member.getMemberId(), name, member.getProfileUrl());
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


    //비밀번호 변경
    public void changePassword(PasswordChangeDto request) {
        Member member = memberRepository.findByBirthdayAndPhone(request.getBirthday(), request.getPhone())
                .orElseThrow(() -> new IllegalArgumentException("일치하는 회원이 없습니다."));
        member.setPassword(encoder.encode(request.getNewPassword()));
        memberRepository.save(member);
    }


    // 현재 로그인한 member 불러오기
    public Member getLoginMember(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phone = authentication.getName();
        return memberRepository.findByPhone(phone)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "인증된 회원 정보가 없습니다."));
    }
    @Transactional(readOnly = true) // JPA 조회 성능 최적화
    public Member getMemberById(Long memberId) {
        // ✅ memberId로 회원 정보 조회
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원 ID " + memberId + "를 찾을 수 없습니다."));
    }
}
