package Jeans.Jeans.Member.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BasicEditRequestDto {
    private Boolean edit1;
    private Boolean edit2;
    private Boolean edit3;
}
