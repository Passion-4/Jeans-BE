package Jeans.Jeans.Member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileUpdateResDto {
    private Long memberId;
    private String profileUrl;
}
