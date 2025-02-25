package Jeans.Jeans.Emoticon.repository;

import Jeans.Jeans.Emoticon.domain.Emoticon;
import Jeans.Jeans.Photo.domain.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface EmoticonRepository extends JpaRepository<Emoticon, Long> {
    void deleteAllByPhoto(Photo photo);
    Optional<Emoticon> findByPhoto(Photo photo);
}
