package Jeans.Jeans.PhotoTag.repository;

import Jeans.Jeans.Photo.domain.Photo;
import Jeans.Jeans.PhotoTag.domain.PhotoTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoTagRepository extends JpaRepository<PhotoTag, Long> {
    void deleteAllByPhoto(Photo photo);
}
