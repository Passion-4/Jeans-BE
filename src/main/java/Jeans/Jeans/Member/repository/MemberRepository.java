package Jeans.Jeans.Member.repository;

import Jeans.Jeans.Member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Boolean existsByPhone(String phone);
    Optional<Member> findByPhone(String phone);
    Optional<Member> findByNameAndPhone(String name, String phone);
    Optional<Member> findByBirthdayAndPhone(String birthday, String phone);

    @Query("SELECT DISTINCT m FROM Member m " +
            "WHERE m IN (SELECT mp.receiver FROM MemberPhoto mp WHERE mp.sharer = :user) " +
            "OR m IN (SELECT mp.sharer FROM MemberPhoto mp WHERE mp.receiver = :user)")
    List<Member> findMembersWithSharedPhotos(@Param("user") Member user);
}
