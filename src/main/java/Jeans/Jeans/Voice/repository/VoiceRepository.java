package Jeans.Jeans.Voice.repository;

import Jeans.Jeans.Photo.domain.Photo;
import Jeans.Jeans.Voice.domain.Voice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoiceRepository extends JpaRepository<Voice, Long> {
    void deleteAllByPhoto(Photo photo);
    List<Voice> findAllByPhoto(Photo photo);
}
