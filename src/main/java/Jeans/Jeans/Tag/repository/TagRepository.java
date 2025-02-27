package Jeans.Jeans.Tag.repository;

import Jeans.Jeans.Photo.domain.Photo;
import Jeans.Jeans.Tag.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag findByName(String tagName);
    List<Tag> findAllByPhoto(Photo photo);
}
