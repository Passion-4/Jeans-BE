package Jeans.Jeans.Member.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

    @Column(nullable = false, length = 6)
    @Size(min = 6, max = 6)
    @Pattern(regexp = "\\d{6}", message = "생년월일은 6자리 숫자여야 합니다.")
    private String birthday;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]+$", message = "비밀번호는 영문과 숫자를 포함해야 합니다.")
    private String password;

    @Column
    private String profileUrl;

    @Builder
    public Member(String name, String birthday, String phone, String password, String profileUrl){
        this.name = name;
        this.birthday = birthday;
        this.phone = phone;
        this.password = password;
        this.profileUrl = profileUrl;
    }
}
