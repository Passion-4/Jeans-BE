package Jeans.Jeans.Member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class SignUpRequestDto {
    private String name;
    private String birthday;
    private String phone;
    private String password;
    private Long voiceType;
}
