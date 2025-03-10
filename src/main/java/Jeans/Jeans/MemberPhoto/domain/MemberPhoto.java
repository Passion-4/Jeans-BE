package Jeans.Jeans.MemberPhoto.domain;

import Jeans.Jeans.Member.domain.Member;
import Jeans.Jeans.Photo.domain.Photo;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long memberPhotoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id", nullable = false)
    private Photo photo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sharer_id", nullable = false)
    private Member sharer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Member receiver;

    @Builder
    public MemberPhoto(Photo photo, Member sharer, Member receiver){
        this.photo = photo;
        this.sharer = sharer;
        this.receiver = receiver;
    }
}
