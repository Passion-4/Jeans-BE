package Jeans.Jeans.Team.service;

import Jeans.Jeans.Member.domain.Member;
import Jeans.Jeans.Member.repository.MemberRepository;
import Jeans.Jeans.Team.domain.Team;
import Jeans.Jeans.Team.dto.CheckResponseDto;
import Jeans.Jeans.Team.dto.TeamDto;
import Jeans.Jeans.Team.dto.TeamRequestDto;
import Jeans.Jeans.Team.repository.TeamRepository;
import Jeans.Jeans.TeamMember.domain.TeamMember;
import Jeans.Jeans.TeamMember.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;
    private final TeamMemberRepository teamMemberRepository;


    // 팀 생성
    public String createTeam(Member user, TeamRequestDto requestDto){
        Team team = new Team(requestDto.getName(), null);
        teamRepository.save(team);

        TeamMember teamMember = new TeamMember(user, team);
        teamMemberRepository.save(teamMember);

        for (Long memberId : requestDto.getMemberList()){
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new EntityNotFoundException("memberId가 " + memberId + "인 회원이 존재하지 않습니다."));
            TeamMember teamMember1 = new TeamMember(member, team);
            teamMemberRepository.save(teamMember1);
        }
        return "팀이 생성되었습니다.";
    }

    // 기존 팀 여부 조회
    public CheckResponseDto checkTeamExists(Member member, List<Long> memberIds) {
        memberIds.add(member.getMemberId());
        long size = memberIds.size();
        List<Long> existingTeams = teamMemberRepository.findTeamsByMemberIds(memberIds, size);

        Boolean check = false;
        Long teamId = null;

        // 이미 존재하는 팀이 있는 경우
        if (!existingTeams.isEmpty()) {
            check = true;
            teamId = existingTeams.get(0);
        }

        return new CheckResponseDto(check, teamId);
    }

    // 팀 정보 조회
    public TeamDto getTeamInfo(Long teamId){
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("teamId가 " + teamId + "인 팀이 존재하지 않습니다."));
        return new TeamDto(teamId, team.getName(), team.getImageUrl());
    }
}
