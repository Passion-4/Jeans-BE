package Jeans.Jeans.TeamMember.repository;

import Jeans.Jeans.TeamMember.domain.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
}
