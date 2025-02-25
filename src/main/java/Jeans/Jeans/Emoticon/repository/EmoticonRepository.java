package Jeans.Jeans.Emoticon.repository;

import Jeans.Jeans.Emoticon.domain.Emoticon;
import Jeans.Jeans.Photo.domain.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmoticonRepository extends JpaRepository<Emoticon, Long> {
    void deleteAllByPhoto(Photo photo);
    Optional<Emoticon> findByPhoto(Photo photo);

    List<Emoticon> findAllByPhoto(Photo photo);
}
