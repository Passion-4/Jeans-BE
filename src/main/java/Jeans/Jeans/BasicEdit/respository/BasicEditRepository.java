package Jeans.Jeans.BasicEdit.respository;

import Jeans.Jeans.BasicEdit.domain.BasicEdit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BasicEditRepository extends JpaRepository<BasicEdit, Long> {
}
