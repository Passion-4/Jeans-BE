package Jeans.Jeans.Member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PasswordChangeDto {
    private String birthday;
    private String phone;
    private String newPassword;
}
