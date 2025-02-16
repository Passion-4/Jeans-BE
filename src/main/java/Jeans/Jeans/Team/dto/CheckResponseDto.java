package Jeans.Jeans.Team.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CheckResponseDto {
    private Boolean check;
    private Long teamId;

    @Builder
    public CheckResponseDto(Boolean check, Long teamId){
        this.check = check;
        this.teamId = teamId;
    }
}
