package Jeans.Jeans.Follow.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestedFollowDto {
    private Long followId;
    private Long memberId;
    private String name;
    private String profileUrl;

    @Builder
    public RequestedFollowDto(Long followId, Long memberId, String name, String profileUrl) {
        this.followId = followId;
        this.memberId = memberId;
        this.name = name;
        this.profileUrl = profileUrl;
    }
}
