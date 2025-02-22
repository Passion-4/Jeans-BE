package Jeans.Jeans.Photo.controller;

import Jeans.Jeans.Member.domain.Member;
import Jeans.Jeans.Member.service.MemberService;
import Jeans.Jeans.Photo.dto.FriendShareReqDto;
import Jeans.Jeans.Photo.dto.PhotoShareResDto;
import Jeans.Jeans.Photo.dto.TeamShareReqDto;
import Jeans.Jeans.Photo.service.PhotoService;
import Jeans.Jeans.global.service.S3Uploader;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
}
