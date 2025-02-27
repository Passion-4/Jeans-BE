package Jeans.Jeans.Photo.controller;

import Jeans.Jeans.Member.domain.Member;
import Jeans.Jeans.Member.service.MemberService;
import Jeans.Jeans.Photo.dto.*;
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

    // 사진에 음성 첨부
    @PostMapping(value = "/photo/voice", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public VoiceMessageResDto createVoiceMessage(@RequestPart(value = "audio") MultipartFile image,
                                           @RequestParam(value = "dto") String dtoJson) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        VoiceMessageReqDto reqDto = objectMapper.readValue(dtoJson, VoiceMessageReqDto.class);
        Member user = memberService.getLoginMember();
        String audioUrl = s3Uploader.upload(image, "photo-voice");
        return photoService.createVoiceMessage(user, audioUrl, reqDto);
    }

    // 내 피드 조회
    @GetMapping("/feed")
    @ResponseStatus(value = HttpStatus.OK)
    public List<FeedPhotoDto> getFeedPhotos(){
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

    // 사진 상세 조회
    @GetMapping("/photos/{photo_id}/detail")
    @ResponseStatus(value = HttpStatus.OK)
    public PhotoDetailDto getPhotoDetail(@PathVariable("photo_id") Long photoId){
        Member user = memberService.getLoginMember();
        return photoService.getPhotoDetail(user, photoId);
    }

    // 이모티콘 전송
    @PostMapping("/photos/{photo_id}/emoticon/{type}")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<String> sendEmoticon(@PathVariable("photo_id") Long photoId, @PathVariable("type") Integer type){
        Member user = memberService.getLoginMember();
        String response = photoService.sendEmoticon(photoId, user, type);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 팀 채팅방에 전송된 이모티콘 목록 조회
    @GetMapping("/team-photos/{photo_id}/emoticons")
    @ResponseStatus(value = HttpStatus.OK)
    public List<EmoticonDto> getEmoticonList(@PathVariable("photo_id") Long photoId){
        return photoService.getEmoticonList(photoId);
    }

    // 기본 보정 결과 확인
    @PostMapping("/photo/basic")
    @ResponseStatus(value = HttpStatus.OK)
    public EditResponseDto getBasicEditResult(@RequestPart(value = "image") MultipartFile image){
        Member member = memberService.getLoginMember();
        return new EditResponseDto("https://원본", "https://보정본");
    }
    
    // 기본 보정 후 사진 선택
    @PatchMapping("/photo/basic/save")
    @ResponseStatus(value = HttpStatus.OK)
    public ResultSelectDto selectBasicEditResult(@RequestBody ResultSelectDto reqDto){
        String response = "기본 보정이 마무리되었습니다.";
        return new ResultSelectDto(reqDto.getPhotoUrl());
    }

    // 동안 보정 결과 확인
    @PatchMapping("/photo/young")
    public EditResponseDto getYoungEditResult(@RequestBody AdditionalEditReqDto reqDto){
        Member member = memberService.getLoginMember();
        return new EditResponseDto("https://원본", "https://보정본");
    }

    // 동안 보정 후 사진 선택
    @PatchMapping("/photo/young/save")
    public ResultSelectDto selectYoungEditResult(@RequestBody ResultSelectDto reqDto){
        return new ResultSelectDto(reqDto.getPhotoUrl());
    }

    // 머리숱 보정 결과 확인
    @PatchMapping("/photo/volume")
    public EditResponseDto getVolumeEditResult(@RequestBody AdditionalEditReqDto reqDto){
        Member member = memberService.getLoginMember();
        return new EditResponseDto("https://원본", "https://보정본");
    }

    // 머리숱 보정 후 사진 선택
    @PatchMapping("/photo/volume/save")
    public ResultSelectDto selectVolumeEditResult(@RequestBody ResultSelectDto reqDto){
        return new ResultSelectDto(reqDto.getPhotoUrl());
    }

    // V라인 보정 결과 확인
    @PatchMapping("/photo/slim")
    public SlimEditResponseDto getSlimEditResult(@RequestBody AdditionalEditReqDto reqDto){
        Member member = memberService.getLoginMember();
        return new SlimEditResponseDto("https://원본", "https://보정본1", "https://보정본2");
    }

    // V라인 보정 후 사진 선택
    @PatchMapping("/photo/slim/save")
    public ResultSelectDto selectSlimEditResult(@RequestBody ResultSelectDto reqDto){
        return new ResultSelectDto(reqDto.getPhotoUrl());
    }

    // 사진에서 사용자의 얼굴 추출
    @PostMapping("/photo/best")
    @ResponseStatus(value = HttpStatus.CREATED)
    public BestPhotoResDto getFaceImages(@RequestPart(value = "image1") MultipartFile image1,
                                         @RequestPart(value = "image2") MultipartFile image2,
                                         @RequestPart(value = "image3") MultipartFile image3,
                                         @RequestPart(value = "image4") MultipartFile image4){
        return new BestPhotoResDto("얼굴1", "얼굴2", "얼굴3", "얼굴4");
    }

    @PostMapping("/photo/best/{order}")
    public ResultSelectDto selectBestPhoto(@PathVariable("order") Long order){
        return new ResultSelectDto("https://해당 순서에 해당하는 원본");
    }

    // 사진의 태그 조회
    @GetMapping("/photo/{photo_id}/tags")
    public TagListResDto getTagList(@PathVariable("photo_id") Long photoId){
        return photoService.getTagList(photoId);
    }
}
