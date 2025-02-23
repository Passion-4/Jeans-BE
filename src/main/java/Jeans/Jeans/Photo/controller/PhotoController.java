package Jeans.Jeans.Photo.controller;

import Jeans.Jeans.Member.domain.Member;
import Jeans.Jeans.Member.service.MemberService;
import Jeans.Jeans.Photo.dto.FriendShareReqDto;
import Jeans.Jeans.Photo.dto.PhotoDto;
import Jeans.Jeans.Photo.dto.PhotoShareResDto;
import Jeans.Jeans.Photo.dto.TeamShareReqDto;
import Jeans.Jeans.Photo.service.PhotoService;
import Jeans.Jeans.global.service.S3Uploader;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PhotoController {
    private final MemberService memberService;
    private final PhotoService photoService;
    private final S3Uploader s3Uploader;

    // 친구에게 사진 공유
    @PostMapping(value = "/photo/friend-share", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PhotoShareResDto shareFriendPhoto(@RequestPart(value = "image") MultipartFile image,
                                             @RequestParam(value = "dto") String dtoJson) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        FriendShareReqDto shareReqDto = objectMapper.readValue(dtoJson, FriendShareReqDto.class);

        Member user = memberService.getLoginMember();
        String photoUrl = s3Uploader.upload(image, "friend-shared-image");
        return photoService.shareFriendPhoto(user, photoUrl, shareReqDto);
    }

    // 팀에게 사진 공유
    @PostMapping(value = "/photo/team-share", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PhotoShareResDto shareTeamPhoto(@RequestPart(value = "image") MultipartFile image,
                                           @RequestParam(value = "dto") String dtoJson) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        TeamShareReqDto shareReqDto = objectMapper.readValue(dtoJson, TeamShareReqDto.class);
        Member user = memberService.getLoginMember();
        String photoUrl = s3Uploader.upload(image, "team-shared-image");
        return photoService.shareTeamPhoto(user, photoUrl, shareReqDto);
    }

    // 사진 공유 취소
    @DeleteMapping("/photos/{photo_id}")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<String> deletePhoto(@PathVariable("photo_id") Long photoId){
        Member member = memberService.getLoginMember();
        photoService.deletePhoto(member, photoId);
        return ResponseEntity.ok("사진이 삭제되었습니다.");
    }

    // 내 피드 조회
    @GetMapping("/feed")
    @ResponseStatus(value = HttpStatus.OK)
    public List<PhotoDto> getFeedPhotos(){
        Member member = memberService.getLoginMember();
        return photoService.getFeedPhotos(member);
    }

    // 친구별 공유한 사진 목록 조회
    @GetMapping("/friend-photos/{member_id}")
    @ResponseStatus(value = HttpStatus.OK)
    public List<PhotoDto> getFriendPhotos(@PathVariable("member_id") Long memberId){
        Member user = memberService.getLoginMember();
        return photoService.getFriendPhotos(user, memberId);
    }

    // 팀별 공유한 사진 목록 조회
    @GetMapping("/team-photos/{team_id}")
    @ResponseStatus(value = HttpStatus.OK)
    public List<PhotoDto> getTeamPhotos(@PathVariable("team_id") Long teamId){
        Member user = memberService.getLoginMember();
        return photoService.getTeamPhotos(user, teamId);
    }
}
