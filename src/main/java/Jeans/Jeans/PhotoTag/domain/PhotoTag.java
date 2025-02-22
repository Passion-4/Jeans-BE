package Jeans.Jeans.PhotoTag.domain;

import Jeans.Jeans.Photo.domain.Photo;
import Jeans.Jeans.Tag.domain.Tag;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhotoTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long photoTagId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id", nullable = false)
    private Photo photo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    @Builder
    public PhotoTag(Photo photo, Tag tag){
        this.photo = photo;
        this.tag = tag;
    }
}
