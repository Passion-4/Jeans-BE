package Jeans.Jeans.Member.dto;

import Jeans.Jeans.Team.dto.TargetTeamDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShareTargetDto {
    List<TargetTeamDto> teamDtoList;
    List<TargetMemberDto> memberDtoList;

    @Builder
    public ShareTargetDto(List<TargetTeamDto> teamDtoList, List<TargetMemberDto> memberDtoList){
        this.teamDtoList = teamDtoList;
        this.memberDtoList = memberDtoList;
    }
}
