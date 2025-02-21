package Jeans.Jeans.Member.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PasswordChangeDto {
    private String birthday;
    private String phone;
    private String newPassword;
}
