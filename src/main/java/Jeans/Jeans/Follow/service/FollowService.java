package Jeans.Jeans.Follow.service;

import Jeans.Jeans.Follow.domain.Follow;
import Jeans.Jeans.Follow.domain.Status;
import Jeans.Jeans.Follow.repository.FollowRepository;
import Jeans.Jeans.Member.domain.Member;
import Jeans.Jeans.Member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            return "팔로우 요청이 수락되었습니다.";
        }
        else{
            return "잘못된 요청입니다.";
        }
    }
}
