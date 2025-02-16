package Jeans.Jeans.Team.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamDto {
    private Long teamId;
    private String name;
    private String imageUrl;

    @Builder
    public TeamDto(Long teamId, String name, String imageUrl){
        this.teamId = teamId;
        this.name = name;
        this.imageUrl = imageUrl;
    }
}
