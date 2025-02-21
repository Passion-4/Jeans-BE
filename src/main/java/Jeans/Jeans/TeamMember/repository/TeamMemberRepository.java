package Jeans.Jeans.TeamMember.repository;

import Jeans.Jeans.Member.domain.Member;
import Jeans.Jeans.TeamMember.domain.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    // 주어진 회원들이 속한 팀이 있는지 확인하는 쿼리
    @Query("SELECT tm.team.teamId FROM TeamMember tm WHERE tm.member.memberId IN :memberIdList " +
            "GROUP BY tm.team.teamId HAVING COUNT(DISTINCT tm.member.memberId) = :size")
    List<Long> findTeamsByMemberIds(@Param("memberIdList") List<Long> memberIdList, @Param("size") long size);
    List<TeamMember> findAllByMember(Member member);
}
