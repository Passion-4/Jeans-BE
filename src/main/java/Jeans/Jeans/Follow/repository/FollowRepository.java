package Jeans.Jeans.Follow.repository;

import Jeans.Jeans.Follow.domain.Follow;
import Jeans.Jeans.Member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Boolean existsByFollowerAndFollowing(Member follower, Member following);
}
