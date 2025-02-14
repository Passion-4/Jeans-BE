package Jeans.Jeans.Member.controller;

import Jeans.Jeans.Member.dto.LoginRequestDto;
import Jeans.Jeans.Member.dto.LoginResponseDto;
import Jeans.Jeans.Member.dto.RefreshRequestDto;
import Jeans.Jeans.Member.dto.SignUpRequestDto;
import Jeans.Jeans.Member.service.MemberService;
import Jeans.Jeans.Member.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;
    private final RefreshTokenService refreshTokenService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequestDto requestDto){
        return ResponseEntity.ok().body(memberService.signUp(requestDto.getName(), requestDto.getBirthday(), requestDto.getPhone(), requestDto.getPassword()));
    }

    // 로그인
    // 성공적으로 로그인한 경우, 회원의 아이디(phone), AccessToken 값, RefreshToken 값을 담은 DTO를 응답함
    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto requestDto){
        return memberService.login(requestDto.getPhone(), requestDto.getPassword());
    }

    // RefreshToken을 이용해 새 AccessToken 발급 요청
    @PostMapping("/refresh")
    public LoginResponseDto refresh(@RequestBody RefreshRequestDto refreshRequestDto){
        return memberService.refresh(refreshRequestDto.getRefreshToken());
    }
}
