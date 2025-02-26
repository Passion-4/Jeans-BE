package Jeans.Jeans.Voice.domain;

import Jeans.Jeans.Member.domain.Member;
import Jeans.Jeans.Photo.domain.Photo;
import Jeans.Jeans.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Voice extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long voiceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id", nullable = false)
    private Photo photo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String voiceUrl;

    @Column(nullable = false)
    private String transcript;

    @Builder
    public Voice(Photo photo, Member member, String voiceUrl, String transcript){
        this.photo = photo;
        this.member = member;
        this.voiceUrl = voiceUrl;
        this.transcript = transcript;
    }
}
