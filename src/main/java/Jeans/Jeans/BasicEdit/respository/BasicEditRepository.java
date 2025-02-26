package Jeans.Jeans.BasicEdit.respository;

import Jeans.Jeans.BasicEdit.domain.BasicEdit;
import Jeans.Jeans.Member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BasicEditRepository extends JpaRepository<BasicEdit, Long> {
    Optional<BasicEdit> findByMember(Member member);
}
