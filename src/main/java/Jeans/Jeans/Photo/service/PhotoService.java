package Jeans.Jeans.Photo.service;

import Jeans.Jeans.Emoticon.repository.EmoticonRepository;
import Jeans.Jeans.Member.domain.Member;
import Jeans.Jeans.Member.repository.MemberRepository;
import Jeans.Jeans.MemberPhoto.domain.MemberPhoto;
import Jeans.Jeans.MemberPhoto.repository.MemberPhotoRepository;
import Jeans.Jeans.Photo.domain.Photo;
import Jeans.Jeans.Photo.dto.*;
import Jeans.Jeans.Photo.repository.PhotoRepository;
import Jeans.Jeans.PhotoTag.domain.PhotoTag;
import Jeans.Jeans.PhotoTag.repository.PhotoTagRepository;
import Jeans.Jeans.Tag.domain.Tag;
import Jeans.Jeans.Tag.repository.TagRepository;
import Jeans.Jeans.Team.domain.Team;
import Jeans.Jeans.Team.repository.TeamRepository;
import Jeans.Jeans.TeamMember.domain.TeamMember;
import Jeans.Jeans.TeamMember.repository.TeamMemberRepository;
import Jeans.Jeans.Voice.repository.VoiceRepository;
import Jeans.Jeans.global.service.S3Uploader;
import com.drew.imaging.ImageProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import com.drew.metadata.Metadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.drew.imaging.ImageMetadataReader; // ✅ 이미지 메타데이터 리더

import com.drew.metadata.exif.ExifSubIFDDirectory;

import org.xml.sax.SAXException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PhotoService {
    private final PhotoRepository photoRepository;
    private final MemberPhotoRepository memberPhotoRepository;
    private final MemberRepository memberRepository;
    private final TagRepository tagRepository;
    private final PhotoTagRepository photoTagRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final VoiceRepository voiceRepository;
    private final EmoticonRepository emoticonRepository;
    private final S3Uploader s3Uploader;


    @Value("${openai.api.key}")
    private String openaiApiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public PhotoShareResDto analyzePhoto(MultipartFile image, String photoUrl) throws IOException {
        String base64Image = encodeToBase64(image);
        String dateTaken = extractDateFromExif(image);

        String prompt = "이 사진의 태그와 제목을 JSON 형식으로 반환해줘. 반드시 한국어로 작성해줘. "
                + "태그는 관련성이 높은 순서대로 최대 3개까지만 생성해야 해. "
                + "제목 형식은 태그와 관련된 목적어와 서술어를 활용해서 표현해 주는데, 10글자는 넘기고, 최대 13글자를 넘기면 안돼.";

        String requestBody = "{ \"model\": \"gpt-4o\", \"messages\": [ "
                + "{\"role\": \"system\", \"content\": \"너는 이미지를 분석해서 태그와 제목을 생성하는 AI야.\"}, "
                + "{\"role\": \"user\", \"content\": ["
                + "{\"type\": \"text\", \"text\": \"" + prompt + "\"},"
                + "{\"type\": \"image_url\", \"image_url\": {\"url\": \"data:image/jpeg;base64," + base64Image + "\"}}"
                + "]}]}";

        String response = restTemplate.postForObject("https://api.openai.com/v1/chat/completions",
                createHttpEntity(requestBody), String.class);

        return parseResponse(response, photoUrl, dateTaken);
    }

    // ✅ Base64 인코딩
    private String encodeToBase64(MultipartFile image) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(image.getBytes());
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    // ✅ EXIF에서 촬영 날짜 추출
    private String extractDateFromExif(MultipartFile image) {
        try (InputStream inputStream = image.getInputStream()) {
            // 메타데이터 읽기
            Metadata metadata = ImageMetadataReader.readMetadata(inputStream);

            // EXIF 정보에서 날짜 추출
            ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            if (directory != null) {
                Date date = directory.getDateOriginal();
                if (date != null) {
                    // yyyy-MM-dd 형식으로 변환하여 반환
                    return new SimpleDateFormat("yyyy-MM-dd").format(date);
                }
            }
        } catch (IOException | ImageProcessingException e) {
            return "날짜 정보 없음";
        }
        return "날짜 정보 없음";
    }

    // ✅ OpenAI API 요청 헤더 설정
    private HttpEntity<String> createHttpEntity(String requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);
        return new HttpEntity<>(requestBody, headers);
    }

    // ✅ OpenAI API 응답 파싱
    private PhotoShareResDto parseResponse(String response, String photoUrl, String dateTaken) throws IOException {
        JsonNode root = objectMapper.readTree(response);
        String content = root.path("choices").get(0).path("message").path("content").asText();

        content = content.replace("```json", "").replace("```", "").trim();
        JsonNode parsedData = objectMapper.readTree(content);

        List<String> tags = new ArrayList<>();
        parsedData.path("태그").forEach(tag -> tags.add(tag.asText()));

        return new PhotoShareResDto(
                photoUrl,
                dateTaken,
                parsedData.path("제목").asText("제목 없음"),
                tags
        );
    }

    // 친구에게 사진 공유
    public PhotoShareResDto shareFriendPhoto(Member user, String photoUrl, FriendShareReqDto shareReqDto, PhotoShareResDto aiData) throws IOException {
        List<Long> receiverList = shareReqDto.getReceiverList();

        // ✅ 사진 엔티티 저장
        Photo photo = new Photo(user, null, photoUrl, aiData.getTitle(), LocalDate.now());
        photoRepository.save(photo);

        // ✅ 친구들에게 공유 내역 저장
        for (Long memberId : receiverList) {
            Member friend = memberRepository.findById(memberId)
                    .orElseThrow(() -> new EntityNotFoundException("memberId가 " + memberId + "인 회원이 존재하지 않습니다."));
            memberPhotoRepository.save(new MemberPhoto(photo, user, friend));
        }

        // ✅ AI가 제공한 태그 저장
        for (String tagName : aiData.getTags()) {
            Tag tag = tagRepository.findByName(tagName);
            if (tag == null) {
                tag = new Tag(tagName);
                tagRepository.save(tag);
            }
            photoTagRepository.save(new PhotoTag(photo, tag));
        }

        return aiData;
    }

    // 팀에게 사진 공유
    public PhotoShareResDto shareTeamPhoto(Member user, String photoUrl, TeamShareReqDto shareReqDto, PhotoShareResDto aiData) throws IOException {
        Long teamId = shareReqDto.getTeamId();
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("teamId가 " + teamId + "인 팀이 존재하지 않습니다."));

        // ✅ 사진 엔티티 저장
        Photo photo = new Photo(user, team, photoUrl, aiData.getTitle(), LocalDate.now());
        photoRepository.save(photo);

        // ✅ AI가 제공한 태그 저장
        for (String tagName : aiData.getTags()) {
            Tag tag = tagRepository.findByName(tagName);
            if (tag == null) {
                tag = new Tag(tagName);
                tagRepository.save(tag);
            }
            photoTagRepository.save(new PhotoTag(photo, tag));
        }

        return aiData;
    }

    // 사진 공유 취소
    @Transactional
    public String deletePhoto(Member member, Long photoId){
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new EntityNotFoundException("photoId가 " + photoId + "인 사진이 존재하지 않습니다."));

        if (!photo.getMember().equals(member)) {
            throw new IllegalArgumentException("해당 사용자가 공유한 사진이 아닙니다.");
        }

        memberPhotoRepository.deleteAllByPhoto(photo);
        photoTagRepository.deleteAllByPhoto(photo);
        voiceRepository.deleteAllByPhoto(photo);
        emoticonRepository.deleteAllByPhoto(photo);

        Team team = photo.getTeam();

        photoRepository.delete(photo);

        if (team != null && !photoRepository.existsByTeam(team)) {
            teamMemberRepository.deleteAllByTeam(team);
            teamRepository.delete(team);
        }

        return "사진이 삭제되었습니다.";
    }

    // 내 피드 조회
    public List<PhotoDto> getFeedPhotos(Member member){
        List<PhotoDto> photoDtoList = new ArrayList<>();
        List<Photo> photoList = new ArrayList<>();

        List<TeamMember> teamMembers = teamMemberRepository.findAllByMember(member);
        List<Team> teams = new ArrayList<>();
        for (TeamMember teamMember : teamMembers){
            teams.add(teamMember.getTeam());
        }
        for (Team team : teams){
            List<Photo> photos = photoRepository.findAllByTeam(team);
            photoList.addAll(photos);
        }

        List<MemberPhoto> memberPhotos = new ArrayList<>();
        Set<Photo> uniquePhotos = new HashSet<>();
        for (MemberPhoto memberPhoto : memberPhotoRepository.findAllBySharer(member)) {
            if (uniquePhotos.add(memberPhoto.getPhoto())) {
                memberPhotos.add(memberPhoto);
            }
        }
        memberPhotos.addAll(memberPhotoRepository.findAllByReceiver(member));
        for(MemberPhoto memberPhoto : memberPhotos){
            photoList.add(memberPhoto.getPhoto());
        }

        photoList.sort(Comparator.comparing(Photo::getPhotoId).reversed());
        for (Photo photo : photoList){
            photoDtoList.add(new PhotoDto(photo.getPhotoId(), photo.getPhotoUrl()));
        }

        return photoDtoList;
    }

    // 친구별 공유한 사진 목록 조회
    public List<PhotoDto> getFriendPhotos(Member user, Long memberId){
        Member friend = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("memberId가 " + memberId + "인 회원이 존재하지 않습니다."));

        List<PhotoDto> photoDtoList = new ArrayList<>();

        List<MemberPhoto> memberPhotos = new ArrayList<>();
        memberPhotos.addAll(memberPhotoRepository.findAllBySharerAndReceiver(user, friend));
        memberPhotos.addAll(memberPhotoRepository.findAllBySharerAndReceiver(friend, user));

        List<Photo> photos = new ArrayList<>();
        for (MemberPhoto memberPhoto : memberPhotos){
            photos.add(memberPhoto.getPhoto());
        }
        photos.sort(Comparator.comparing(Photo::getCreatedDate).reversed());

        for(Photo photo : photos){
            photoDtoList.add(new PhotoDto(photo.getPhotoId(), photo.getPhotoUrl()));
        }
        return photoDtoList;
    }

    // 팀별 공유한 사진 목록 조회
    public List<PhotoDto> getTeamPhotos(Member user, Long teamId){
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("teamId가 " + teamId + "인 팀이 존재하지 않습니다."));

        List<PhotoDto> photoDtoList = new ArrayList<>();

        List<Photo> photos = photoRepository.findAllByTeam(team);
        photos.sort(Comparator.comparing(Photo::getCreatedDate).reversed());

        for(Photo photo : photos){
            photoDtoList.add(new PhotoDto(photo.getPhotoId(), photo.getPhotoUrl()));
        }
        return photoDtoList;
    }
}
