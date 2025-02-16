package Jeans.Jeans.Member.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FollowTargetDto {
    private Long memberId;
    private String name;
    private String profileUrl;

    @Builder
    public FollowTargetDto(Long memberId, String name, String profileUrl){
        this.memberId = memberId;
        this.name = name;
        this.profileUrl = profileUrl;
    }
}
