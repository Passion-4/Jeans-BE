package Jeans.Jeans.Member.repository;

import Jeans.Jeans.Member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Boolean existsByPhone(String phone);
}
