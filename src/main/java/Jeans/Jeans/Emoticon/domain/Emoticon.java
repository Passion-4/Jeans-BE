package Jeans.Jeans.Emoticon.domain;

import Jeans.Jeans.Member.domain.Member;
import Jeans.Jeans.Photo.domain.Photo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Emoticon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long emoticonId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id", nullable = false)
    private Photo photo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Member sender;

    @Column(nullable = false)
    private Integer emojiType;

    @Builder
    public Emoticon(Photo photo, Member sender, Integer emojiType){
        this.photo = photo;
        this.sender = sender;
        this.emojiType = emojiType;
    }

    public void updateEmojiType(Integer emojiType){
        this.emojiType = emojiType;
    }
}
