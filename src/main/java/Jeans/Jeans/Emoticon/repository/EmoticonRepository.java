package Jeans.Jeans.Emoticon.repository;

import Jeans.Jeans.Emoticon.domain.Emoticon;
import Jeans.Jeans.Photo.domain.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmoticonRepository extends JpaRepository<Emoticon, Long> {
    void deleteAllByPhoto(Photo photo);
}
