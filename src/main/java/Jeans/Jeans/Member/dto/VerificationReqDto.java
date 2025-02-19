package Jeans.Jeans.Member.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VerificationReqDto {
    private String phone;
    private String randomNumber;
}
