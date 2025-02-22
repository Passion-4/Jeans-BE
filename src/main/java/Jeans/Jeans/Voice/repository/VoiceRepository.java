package Jeans.Jeans.Voice.repository;

import Jeans.Jeans.Photo.domain.Photo;
import Jeans.Jeans.Voice.domain.Voice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoiceRepository extends JpaRepository<Voice, Long> {
    void deleteAllByPhoto(Photo photo);
}
