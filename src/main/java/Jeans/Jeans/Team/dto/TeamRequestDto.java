package Jeans.Jeans.Team.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamRequestDto {
    private String name;
    private List<Long> memberList;
}
