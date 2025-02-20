package Jeans.Jeans.Member.repository;

import Jeans.Jeans.Member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Boolean existsByPhone(String phone);
    Optional<Member> findByPhone(String phone);
    Optional<Member> findByNameAndPhone(String name, String phone);
    Optional<Member> findByBirthdayAndPhone(String birthday, String phone);

}
