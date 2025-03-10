package Jeans.Jeans.Follow.controller;

import Jeans.Jeans.Follow.dto.FriendDto;
import Jeans.Jeans.Follow.dto.NicknameRequestDto;
import Jeans.Jeans.Follow.dto.RequestedFollowDto;
import Jeans.Jeans.Follow.service.FollowService;
import Jeans.Jeans.Member.domain.Member;
import Jeans.Jeans.Member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FollowController {
    private final MemberService memberService;
    private final FollowService followService;

    // 팔로우 요청
    @PostMapping("/follow/{member_id}")
    public ResponseEntity<String> createFollow(@PathVariable("member_id") Long memberId) {
        Member user = memberService.getLoginMember();
        String response = followService.createFollow(user, memberId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 팔로우 요청 수락
    @PostMapping("/follow/requests/{follow_id}")
    public ResponseEntity<String> acceptFollow(@PathVariable("follow_id") Long followId) {
        Member user = memberService.getLoginMember();
        String response = followService.acceptFollow(user, followId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 팔로우 요청 거절
    @DeleteMapping("/follow/requests/{follow_id}")
    public ResponseEntity<String> rejectFollow(@PathVariable("follow_id") Long followId) {
        Member user = memberService.getLoginMember();
        String response = followService.rejectFollow(user, followId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 받은 팔로우 요청 목록 조회
    @GetMapping("/follow/requests")
    @ResponseStatus(value = HttpStatus.OK)
    public List<RequestedFollowDto> getRequestedFollowList() {
        Member member = memberService.getLoginMember();
        return followService.getRequestedFollowList(member);
    }

    // 친구 목록 조회
    @GetMapping("/follow-list")
    @ResponseStatus(value = HttpStatus.OK)
    public List<FriendDto> getFriendList() {
        Member member = memberService.getLoginMember();
        return followService.getFriendList(member);
    }

    // 친구 삭제
    @DeleteMapping("/follow-list/{member_id}")
    @ResponseStatus(value = HttpStatus.OK)
    public String deleteFriend(@PathVariable("member_id") Long memberId) {
        Member user = memberService.getLoginMember();
        followService.deleteFriend(user, memberId);
        return "친구가 삭제되었습니다.";
    }

    // 별명 수정
    @PatchMapping("/follow-list/nickname")
    @ResponseStatus(value = HttpStatus.OK)
    public String updateNickname(@RequestBody NicknameRequestDto requestDto){
        Member member = memberService.getLoginMember();
        followService.updateNickname(member, requestDto);
        return "별명이 수정되었습니다.";
    }

}
