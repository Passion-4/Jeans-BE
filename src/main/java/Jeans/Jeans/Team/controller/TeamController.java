package Jeans.Jeans.Team.controller;

import Jeans.Jeans.Member.domain.Member;
import Jeans.Jeans.Member.service.MemberService;
import Jeans.Jeans.Team.dto.TeamDto;
import Jeans.Jeans.Team.dto.TeamRequestDto;
import Jeans.Jeans.Team.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TeamController {
    private final MemberService memberService;
    private final TeamService teamService;

    // 팀 생성
    @PostMapping("/team")
    public ResponseEntity<String> createTeam(@RequestBody TeamRequestDto teamRequestDto){
        Member member = memberService.getLoginMember();
        String response = teamService.createTeam(member, teamRequestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 팀 정보 조회
    @GetMapping("/team/{team_id}")
    @ResponseStatus(value = HttpStatus.OK)
    public TeamDto getTeamInfo(@PathVariable("team_id") Long teamId){
        return teamService.getTeamInfo(teamId);
    }
}
