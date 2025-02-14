package Jeans.Jeans.Member.controller;

import Jeans.Jeans.Member.dto.SignUpRequestDto;
import Jeans.Jeans.Member.service.MemberService;
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

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequestDto requestDto){
        return ResponseEntity.ok().body(memberService.signUp(requestDto.getName(), requestDto.getBirthday(), requestDto.getPhone(), requestDto.getPassword()));
    }
}
