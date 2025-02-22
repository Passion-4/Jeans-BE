package Jeans.Jeans.Photo.domain;

import Jeans.Jeans.Member.domain.Member;
import Jeans.Jeans.Team.domain.Team;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long photoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(nullable = false)
    private String photoUrl;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate photoDate;

    @Builder
    public Photo(Member member, Team team, String photoUrl, String title, LocalDate photoDate){
        this.member = member;
        this.team = team;
        this.photoUrl = photoUrl;
        this.title = title;
        this.photoDate = photoDate;
    }
}
