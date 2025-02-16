package Jeans.Jeans.Member.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BasicEditResponseDto {
    private Long memberId;
    private Boolean exists;

    @Builder
    public BasicEditResponseDto(Long memberId, Boolean exists){
        this.memberId = memberId;
        this.exists = exists;
    }
}
