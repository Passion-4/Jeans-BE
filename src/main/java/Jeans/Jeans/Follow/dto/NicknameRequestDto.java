package Jeans.Jeans.Follow.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NicknameRequestDto {
    private Long memberId;
    private String nickname;
}
