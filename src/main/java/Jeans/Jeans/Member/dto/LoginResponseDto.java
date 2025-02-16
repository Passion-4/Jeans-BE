package Jeans.Jeans.Member.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginResponseDto {
    private Long memberId;
    private String phone;
    private String accessToken;
    private String refreshToken;

    @Builder
    public LoginResponseDto(Long memberId, String phone, String accessToken, String refreshToken) {
        this.memberId = memberId;
        this.phone = phone;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
