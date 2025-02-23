package Jeans.Jeans.Team.service;

import Jeans.Jeans.Member.domain.Member;
import Jeans.Jeans.Member.repository.MemberRepository;
import Jeans.Jeans.Team.domain.Team;
import Jeans.Jeans.Team.dto.*;
import Jeans.Jeans.Team.repository.TeamRepository;
import Jeans.Jeans.TeamMember.domain.TeamMember;
import Jeans.Jeans.TeamMember.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    // 팀명 수정
    @Transactional
    public String updateTeamName(TeamNameUpdateReqDto requestDto) {
        Team team = teamRepository.findById(requestDto.getTeamId())
                .orElseThrow(() -> new IllegalArgumentException("해당 팀이 존재하지 않습니다."));

        team.updateName(requestDto.getName());
        return "팀명이 수정되었습니다.";
    }

    // 팀 사진 수정
    @Transactional
    public TeamImageUpdateResDto updateTeamImage(Member member, String imageUrl, TeamImageUpdateReqDto updateReqDto){
        Long teamId = updateReqDto.getTeamId();
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("teamId가 " + teamId + "인 회원이 존재하지 않습니다."));

        if (!teamMemberRepository.existsByMemberAndTeam(member, team)) {
            throw new EntityNotFoundException("사용자는 해당 팀의 멤버가 아닙니다.");
        }

        team.updateImage(imageUrl);
        teamRepository.save(team);

        return new TeamImageUpdateResDto(teamId, imageUrl);
    }

    // 기존 팀 여부 조회
    public CheckResponseDto checkTeamExists(Member member, List<Long> memberIds) {
        memberIds.add(member.getMemberId());
        List<Team> exactTeams = findTeamsWithExactMembers(memberIds);
        if (!exactTeams.isEmpty()) {
            // 일치하는 팀이 있는 경우
            return new CheckResponseDto(true, exactTeams.get(0).getTeamId());
        }
        else {
            // 일치하는 팀이 없는 경우
            return new CheckResponseDto(false, null);
        }
    }

    public List<Team> findTeamsWithExactMembers(List<Long> memberIds) {
        // 팀에 속한 모든 TeamMember 정보 조회
        List<TeamMember> allTeamMembers = teamMemberRepository.findAll();

        // 각 팀을 그룹화하해서 그 팀에 속한 멤버들의 memberId만 추출
        Map<Team, List<Long>> teamMemberMap = new HashMap<>();

        for (TeamMember teamMember : allTeamMembers) {
            Team team = teamMember.getTeam();
            Long memberId = teamMember.getMember().getMemberId();

            teamMemberMap.computeIfAbsent(team, k -> new ArrayList<>()).add(memberId);
        }

        // 해당 memberId 리스트와 정확히 일치하는 팀만 반환
        List<Team> exactTeams = new ArrayList<>();
        for (Map.Entry<Team, List<Long>> entry : teamMemberMap.entrySet()) {
            List<Long> teamMembers = entry.getValue();
            if (teamMembers.size() == memberIds.size() && new HashSet<>(teamMembers).containsAll(memberIds)) {
                exactTeams.add(entry.getKey());
            }
        }

        return exactTeams;
    }


    // 팀 정보 조회
    public TeamDto getTeamInfo(Long teamId){
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("teamId가 " + teamId + "인 팀이 존재하지 않습니다."));
        return new TeamDto(teamId, team.getName(), team.getImageUrl());
    }
}
