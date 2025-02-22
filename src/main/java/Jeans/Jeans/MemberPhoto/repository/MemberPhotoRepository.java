package Jeans.Jeans.MemberPhoto.repository;

import Jeans.Jeans.MemberPhoto.domain.MemberPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberPhotoRepository extends JpaRepository<MemberPhoto, Long> {
}
