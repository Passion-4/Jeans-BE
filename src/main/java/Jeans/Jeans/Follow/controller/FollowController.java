package Jeans.Jeans.Follow.controller;

import Jeans.Jeans.Follow.service.FollowService;
import Jeans.Jeans.Member.domain.Member;
import Jeans.Jeans.Member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FollowController {
    private final MemberService memberService;
    private final FollowService followService;

    // 팔로우 요청
    @PostMapping("/follow/{member_id}")
    public ResponseEntity<String> createFollow(@PathVariable("member_id") Long memberId){
        Member user = memberService.getLoginMember();
        String response = followService.createFollow(user, memberId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 팔로우 요청 수락
    @PostMapping("/follow/requests/{follow_id}")
    public ResponseEntity<String> acceptFollow(@PathVariable("follow_id") Long followId){
        Member user = memberService.getLoginMember();
        String response = followService.acceptFollow(user, followId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
