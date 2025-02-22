package Jeans.Jeans.Team.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long teamId;

    @Column(nullable = false)
    private String name;

    @Column
    private String imageUrl;

    @Builder
    public Team(String name, String imageUrl){
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public void updateName(String name){
        this.name = name;
    }

    public void updateImage(String imageUrl){
        this.imageUrl = imageUrl;
    }
}
