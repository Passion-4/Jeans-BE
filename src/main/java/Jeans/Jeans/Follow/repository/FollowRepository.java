package Jeans.Jeans.Follow.repository;

import Jeans.Jeans.Follow.domain.Follow;
import Jeans.Jeans.Follow.domain.Status;
import Jeans.Jeans.Member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Boolean existsByFollowerAndFollowing(Member follower, Member following);
    List<Follow> findAllByFollowingAndStatusOrderByFollowIdDesc(Member following, Status status);
    List<Follow> findAllByFollowerAndStatusOrderByFollowIdDesc(Member follower, Status status);
    Follow findByFollowerAndFollowing(Member follower, Member following);
    List<Follow> findAllByFollowerAndStatus(Member follower, Status status);
}
