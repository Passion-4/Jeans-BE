package Jeans.Jeans.Team.controller;

import Jeans.Jeans.Member.domain.Member;
import Jeans.Jeans.Member.service.MemberService;
import Jeans.Jeans.Team.dto.*;
import Jeans.Jeans.Team.service.TeamService;
import Jeans.Jeans.global.exception.ErrorCode;
import Jeans.Jeans.global.service.S3Uploader;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TeamController {
    private final MemberService memberService;
    private final TeamService teamService;
    private final S3Uploader s3Uploader;

    // 팀 생성
    @PostMapping("/team")
    public TeamResponseDto createTeam(@RequestBody TeamRequestDto teamRequestDto){
        Member member = memberService.getLoginMember();
        return teamService.createTeam(member, teamRequestDto);
    }

    // 팀명 수정
    @PatchMapping("/team/name")
    public ResponseEntity<String> updateTeamName(@RequestBody TeamNameUpdateReqDto requestDto){
        String response = teamService.updateTeamName(requestDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 팀 사진 수정
    @PatchMapping(value = "/team/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public TeamImageUpdateResDto updateTeamImage(@RequestPart(value = "image") MultipartFile image,
                                               @RequestParam(value = "dto") String dtoJson) throws IOException {
        Member member = memberService.getLoginMember();
        if (member == null)
            throw new ResponseStatusException(ErrorCode.NON_LOGIN.getStatus(), ErrorCode.NON_LOGIN.getMessage());

        ObjectMapper objectMapper = new ObjectMapper();
        TeamImageUpdateReqDto updateReqDto = objectMapper.readValue(dtoJson, TeamImageUpdateReqDto.class);

        String imageUrl = s3Uploader.upload(image, "team-profile");
        return teamService.updateTeamImage(member, imageUrl, updateReqDto);
    }

    // 기존 팀 여부 조회
    @GetMapping("/team/check")
    public CheckResponseDto checkTeamExists(@RequestParam("member-id") List<Long> memberIds){
        Member member = memberService.getLoginMember();
        return teamService.checkTeamExists(member, memberIds);
    }

    // 팀 정보 조회
    @GetMapping("/team/{team_id}")
    @ResponseStatus(value = HttpStatus.OK)
    public TeamDto getTeamInfo(@PathVariable("team_id") Long teamId){
        return teamService.getTeamInfo(teamId);
    }
}
