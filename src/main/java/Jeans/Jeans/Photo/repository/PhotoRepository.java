package Jeans.Jeans.Photo.repository;

import Jeans.Jeans.Member.domain.Member;
import Jeans.Jeans.Photo.domain.Photo;
import Jeans.Jeans.Team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findAllByTeam(Team team);
    Boolean existsByTeam(Team team);
    List<Photo> findByTeam_TeamIdIn(List<Long> teamIds);
    Photo findTopByTeamOrderByCreatedDateDesc(Team team);

}
