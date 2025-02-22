package Jeans.Jeans.Tag.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long tagId;

    @Column(nullable = false)
    private String name;

    @Builder
    public Tag(String name){
        this.name = name;
    }
}
