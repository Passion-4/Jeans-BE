package Jeans.Jeans.Member.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TargetMemberDto {
    private Long memberId;
    private String name;
    private String profileUrl;
    private String nickname;

    @Builder
    public TargetMemberDto(Long memberId, String name, String profileUrl, String nickname){
        this.memberId = memberId;
        this.name = name;
        this.profileUrl = profileUrl;
        this.nickname = nickname;
    }
}
