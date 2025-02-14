package Jeans.Jeans.Member.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long memberId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate birthday;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String password;

    @Column
    private String profileUrl;

    @Builder
    public Member(String name, LocalDate birthday, String phone, String password, String profileUrl){
        this.name = name;
        this.birthday = birthday;
        this.phone = phone;
        this.password = password;
        this.profileUrl = profileUrl;
    }
}
