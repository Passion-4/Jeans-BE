package Jeans.Jeans.Team.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamResponseDto {
    private Long teamId;

    @Builder
    public TeamResponseDto(Long teamId){
        this.teamId = teamId;
    }
}
