package Jeans.Jeans.Follow.domain;

import Jeans.Jeans.Member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long followId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private Member follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id", nullable = false)
    private Member following;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    private String nickname;

    @Builder
    public Follow(Member follower, Member following, Status status, String nickname){
        this.follower = follower;
        this.following = following;
        this.status = status;
        this.nickname = nickname;
    }

    public void updateStatus(Status status) {
        this.status = status;
    }
}
