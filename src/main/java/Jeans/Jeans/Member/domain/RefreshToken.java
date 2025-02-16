package Jeans.Jeans.Member.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refreshTokenId;

    // 토큰의 주인인 member의 고유 키
    @Column(nullable = false)
    private Long memberId;

    // 토큰의 값
    @Column(nullable = false)
    private String value;
}
