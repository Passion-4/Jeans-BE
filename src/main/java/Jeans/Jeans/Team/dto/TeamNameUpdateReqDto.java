package Jeans.Jeans.Team.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamNameUpdateReqDto {
    private Long teamId;
    private String name;
}
