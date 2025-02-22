package Jeans.Jeans.MemberPhoto.repository;

import Jeans.Jeans.Member.domain.Member;
import Jeans.Jeans.MemberPhoto.domain.MemberPhoto;
import Jeans.Jeans.Photo.domain.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberPhotoRepository extends JpaRepository<MemberPhoto, Long> {
    List<MemberPhoto> findAllBySharer(Member sharer);
    List<MemberPhoto> findAllByReceiver(Member receiver);
    void deleteAllByPhoto(Photo photo);
}
