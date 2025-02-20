package Jeans.Jeans.Member.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginResponseDto {
    private Long memberId;
    private Integer age;
    private Boolean exists;
    private String phone;
    private String accessToken;
    private String refreshToken;

    @Builder
    public LoginResponseDto(Long memberId, Integer age, Boolean exists, String phone, String accessToken, String refreshToken) {
        this.memberId = memberId;
        this.age = age;
        this.exists = exists;
        this.phone = phone;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
