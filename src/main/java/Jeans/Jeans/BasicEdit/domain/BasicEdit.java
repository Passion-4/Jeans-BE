package Jeans.Jeans.BasicEdit.domain;

import Jeans.Jeans.Member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BasicEdit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long basicEditId;

    @OneToOne(optional = false)
    @JoinColumn(name = "member_id", unique = true, nullable = false)
    private Member member;

    @Column(nullable = false)
    private Boolean edit1;

    @Column(nullable = false)
    private Boolean edit2;

    @Column(nullable = false)
    private Boolean edit3;

    @Column(nullable = false)
    private Boolean edit4;

    @Column(nullable = false)
    private Boolean edit5;
}
