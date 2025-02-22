package Jeans.Jeans.Team.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamImageUpdateResDto {
    private Long teamId;
    private String imageUrl;

    @Builder
    public TeamImageUpdateResDto(Long teamId, String imageUrl){
        this.teamId = teamId;
        this.imageUrl = imageUrl;
    }
}
