package Jeans.Jeans.Member.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberCheckResDto {
    private Long memberId;

    @Builder
    public MemberCheckResDto(Long memberId){
        this.memberId = memberId;
    }
}
