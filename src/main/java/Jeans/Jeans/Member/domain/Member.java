package Jeans.Jeans.Member.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Getter
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
    private String password;

    @Column(nullable = false)
    private Long voiceType;

    @Column
    private String profileUrl;

    @Builder
    public Member(String name, String birthday, String phone, String password, Long voiceType, String profileUrl){
        this.name = name;
        this.birthday = birthday;
        this.phone = phone;
        this.password = password;
        this.voiceType = voiceType;
        this.profileUrl = profileUrl;
    }

    public void updateProfile(String profileUrl){
        this.profileUrl = profileUrl;
    }

    public void updateName(String name){
        this.name = name;
    }

    public void updatePassword(String password){
        this.password = password;
    }
}
