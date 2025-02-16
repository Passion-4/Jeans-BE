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
}
