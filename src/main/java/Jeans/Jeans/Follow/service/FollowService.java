package Jeans.Jeans.Follow.service;

import Jeans.Jeans.Follow.domain.Follow;
import Jeans.Jeans.Follow.domain.Status;
import Jeans.Jeans.Follow.dto.FriendDto;
import Jeans.Jeans.Follow.dto.NicknameRequestDto;
import Jeans.Jeans.Follow.dto.RequestedFollowDto;
import Jeans.Jeans.Follow.repository.FollowRepository;
import Jeans.Jeans.Member.domain.Member;
import Jeans.Jeans.Member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public String createFollow(Member user, Long memberId){
        Member following = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("memberId가 " + memberId + "인 회원이 존재하지 않습니다."));
        if(followRepository.existsByFollowerAndFollowing(user, following)){
            return "이미 보낸 팔로우 요청이 존재합니다.";
        }
        else{
            Follow follow = new Follow(user, following, Status.WAIT, "친구");
            followRepository.save(follow);
            return "팔로우가 요청되었습니다.";
        }
    }

    // 팔로우 요청 수락
    @Transactional
    public String acceptFollow(Member user, Long followId){
        Follow firstFollow = followRepository.findById(followId)
                .orElseThrow(() -> new EntityNotFoundException("followId가 " + followId + "인 팔로우 요청이 존재하지 않습니다."));

        // 해당 팔로우의 following 회원과 로그인한 사용자의 일치 여부
        if(firstFollow.getFollowing().equals(user)){
            firstFollow.updateStatus(Status.FRIEND);
            followRepository.save(firstFollow);

            Follow secondFollow = new Follow(user, firstFollow.getFollower(), Status.FRIEND, "친구");
            followRepository.save(secondFollow);
            return "팔로우 요청이 수락되었습니다.";
        }
        else{
            return "잘못된 요청입니다.";
        }
    }

    // 팔로우 요청 거절
    @Transactional
    public String rejectFollow(Member user, Long followId){
        Follow follow = followRepository.findById(followId)
                .orElseThrow(() -> new EntityNotFoundException("followId가 " + followId + "인 팔로우 요청이 존재하지 않습니다."));

        // 해당 팔로우의 following 회원과 로그인한 사용자의 일치 여부 확인
        // 팔로우 요청의 status가 WAIT인지 확인
        if(follow.getFollowing().equals(user)&&follow.getStatus()==Status.WAIT){
            followRepository.delete(follow);
            return "팔로우 요청이 거절되었습니다.";
        }
        else{
            return "잘못된 요청입니다.";
        }
    }

    // 받은 팔로우 요청 목록 조회
    public List<RequestedFollowDto> getRequestedFollowList(Member member){
        List<RequestedFollowDto> followList = new ArrayList<>();

        // 사용자가 받은 팔로우 요청들을 최신순으로 정렬한 리스트
        List<Follow> follows = new ArrayList<>();
        follows = followRepository.findAllByFollowingAndStatusOrderByFollowIdDesc(member, Status.WAIT);
        for (Follow follow : follows){
            Member follower = follow.getFollower();
            followList.add(new RequestedFollowDto(follow.getFollowId(), follower.getMemberId(), follower.getName(), follower.getProfileUrl()));
        }
        return followList;
    }

    // 친구 목록 조회
    public List<FriendDto> getFriendList(Member member){
        List<FriendDto> friendDtoList = new ArrayList<>();

        List<Follow> follows = new ArrayList<>();
        follows = followRepository.findAllByFollowerAndStatusOrderByFollowIdDesc(member, Status.FRIEND);
        for (Follow follow : follows){
            Member friend = follow.getFollowing();
            friendDtoList.add(new FriendDto(friend.getMemberId(), friend.getName(), friend.getProfileUrl(), follow.getNickname()));
        }
        return friendDtoList;
    }

    // 친구 삭제
    public void deleteFriend(Member user, Long memberId){
        Member friend = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("memberId가 " + memberId + "인 회원이 존재하지 않습니다."));
        Follow follow = followRepository.findByFollowerAndFollowing(user, friend);
        followRepository.delete(follow);
        follow = followRepository.findByFollowerAndFollowing(friend, user);
        followRepository.delete(follow);
    }

    // 별명 수정
    public void updateNickname(Member user, NicknameRequestDto requestDto){
        Long memberId = requestDto.getMemberId();
        Member friend = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("memberId가 " + memberId + "인 회원이 존재하지 않습니다."));
        Follow follow = followRepository.findByFollowerAndFollowing(user, friend);
        follow.updateNickname(requestDto.getNickname());
        followRepository.save(follow);
    }
}
