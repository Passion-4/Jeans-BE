package Jeans.Jeans.Member.controller;

import Jeans.Jeans.Member.domain.Member;
import Jeans.Jeans.Member.dto.*;
import Jeans.Jeans.Member.service.MemberService;
import Jeans.Jeans.Member.service.MessageService;
import Jeans.Jeans.Member.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final RefreshTokenService refreshTokenService;
    private final MessageService messageService;

    // 회원가입
    @PostMapping("/members/signup")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequestDto requestDto){
        return ResponseEntity.ok().body(memberService.signUp(requestDto.getName(), requestDto.getBirthday(), requestDto.getPhone(), requestDto.getPassword()));
    }

    // 로그인
    // 성공적으로 로그인한 경우, 회원의 아이디(phone), AccessToken 값, RefreshToken 값을 담은 DTO를 응답함
    @PostMapping("/members/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto requestDto){
        return memberService.login(requestDto.getPhone(), requestDto.getPassword());
    }

    // RefreshToken을 이용해 새 AccessToken 발급 요청
    @PostMapping("/members/refresh")
    public LoginResponseDto refresh(@RequestBody RefreshRequestDto refreshRequestDto){
        return memberService.refresh(refreshRequestDto.getRefreshToken());
    }

    // 로그아웃
    // 전달받은 RefreshToken을 DB에서 삭제
    @DeleteMapping("/members/logout")
    public String logout(@RequestBody RefreshRequestDto refreshRequestDto) {
        refreshTokenService.deleteRefreshToken(refreshRequestDto.getRefreshToken());
        return "로그아웃되었습니다.";
    }

    // 회원탈퇴
    @DeleteMapping("/members/delete")
    public ResponseEntity<String> deleteMember(Authentication authentication){
        return ResponseEntity.ok().body(memberService.delete(authentication));
    }

    // 기본 보정 설정
    @PostMapping("/my/basic")
    public ResponseEntity<String> createBasicEdit(@RequestBody BasicEditRequestDto requestDto){
        Member member = memberService.getLoginMember();
        memberService.createBasicEdit(member, requestDto);
        return new ResponseEntity<>("memberId가 " + member.getMemberId() + "인 member의 보정 선호 정보가 저장되었습니다.", HttpStatus.CREATED);
    }

    // 기본 보정 설정 변경
    @PatchMapping("/my/basic")
    public ResponseEntity<String> updateBasicEdit(@RequestBody BasicEditRequestDto requestDto){
        Member member = memberService.getLoginMember();
        memberService.updateBasicEdit(member, requestDto);
        return new ResponseEntity<>("memberId가 " + member.getMemberId() + "인 member의 보정 선호 정보가 변경되었습니다.", HttpStatus.OK);
    }

    // 기본 보정 설정 여부 조회
    @GetMapping("members/basic")
    public BasicEditResponseDto existsByBasicEdit(){
        Member member = memberService.getLoginMember();
        return memberService.existsByBasicEdit(member);
    }

    // 팔로우 할 회원 검색
    @GetMapping("/members/search")
    @ResponseStatus(value = HttpStatus.OK)
    public FollowTargetDto getFollowTarget(@RequestParam String name, @RequestParam String phone){
        return memberService.getFollowTarget(name, phone);
    }

    // 인증번호 요청
    @PostMapping("/code/request")
    public ResponseEntity<String> sendSms(@RequestBody CodeRequestDto requestDto){
        String response = messageService.sendSms(requestDto.getPhone());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 인증번호 일치 여부 확인
    @PostMapping("/code/verify")
    public ResponseEntity<Map<String, Object>> verifySmsCode(@RequestBody VerificationReqDto requestDto) {
        Map<String, Object> response = new HashMap<>();

        if (messageService.checkVerification(requestDto)) {
            response.put("success", false);
            response.put("message", "인증번호가 일치하지 않습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        messageService.deleteSmsCertification(requestDto.getPhone());

        response.put("success", true);
        response.put("message", "인증 완료되었습니다.");
        return ResponseEntity.ok(response);
    }
}
