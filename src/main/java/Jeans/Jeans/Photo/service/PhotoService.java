package Jeans.Jeans.Photo.service;

import Jeans.Jeans.Emoticon.domain.Emoticon;
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
import Jeans.Jeans.Voice.domain.Voice;
import Jeans.Jeans.Voice.dto.VoiceDto;
import Jeans.Jeans.Voice.repository.VoiceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Slf4j
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

    // 친구에게 사진 공유
    public PhotoShareResDto shareFriendPhoto(Member user, String photoUrl, FriendShareReqDto shareReqDto){
        List<Long> receiverList = shareReqDto.getReceiverList();

        Photo photo = new Photo(user, null, photoUrl, "테스트 제목", LocalDate.of(2025, 2, 23));
        photoRepository.save(photo);

        for (Long memberId : receiverList){
            Member friend = memberRepository.findById(memberId)
                    .orElseThrow(() -> new EntityNotFoundException("memberId가 " + memberId + "인 회원이 존재하지 않습니다."));
            memberPhotoRepository.save(new MemberPhoto(photo, user, friend));
        }

        List<String> tagNameList = new ArrayList<>(Arrays.asList("테스트 태그1", "테스트 태그2", "테스트 태그3"));

        for (String tagName : tagNameList){
            Tag tag = tagRepository.findByName(tagName);

            if (tag == null) {
                tag = new Tag(tagName);
                tagRepository.save(tag);
            }

            photoTagRepository.save(new PhotoTag(photo, tag));
        }
        return new PhotoShareResDto(photoUrl);
    }

    // 팀에게 사진 공유
    public PhotoShareResDto shareTeamPhoto(Member user, String photoUrl, TeamShareReqDto shareReqDto){
        Long teamId = shareReqDto.getTeamId();
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("teamId가 " + teamId + "인 팀이 존재하지 않습니다."));
        Photo photo = new Photo(user, team, photoUrl, "테스트 제목", LocalDate.of(2025, 2, 23));
        photoRepository.save(photo);

        List<String> tagNameList = new ArrayList<>(Arrays.asList("테스트 태그1", "테스트 태그2", "테스트 태그3"));

        for (String tagName : tagNameList){
            Tag tag = tagRepository.findByName(tagName);

            if (tag == null) {
                tag = new Tag(tagName);
                tagRepository.save(tag);
            }

            photoTagRepository.save(new PhotoTag(photo, tag));
        }
        return new PhotoShareResDto(photoUrl);
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

    // 개인에게 공유한 사진 상세 조회
    public FriendPhotoDetailDto getFriendPhotoDetail(Member user, Long photoId) {
        List<VoiceDto> voiceDtoList = new ArrayList<>();
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new EntityNotFoundException("photoId가 " + photoId + "인 사진이 존재하지 않습니다."));
        List<Voice> voices = voiceRepository.findAllByPhoto(photo);
        Boolean isUser = false;

        List<MemberPhoto> memberPhotos = memberPhotoRepository.findAllByPhoto(photo);
        Member friend;
        for (MemberPhoto memberPhoto : memberPhotos) {
            if (!memberPhoto.getSharer().equals(user)) {
                friend = memberPhoto.getSharer();
            } else {
                friend = memberPhoto.getReceiver();
            }
        }

        Optional<Emoticon> emoticonOptional = emoticonRepository.findByPhoto(photo);
        Emoticon emoticon = null;
        Integer emojiType = 0;
        if (emoticonOptional.isPresent()) {
            emoticon = emoticonOptional.get();
            emojiType = emoticon.getEmojiType();
        }

        for (Voice voice : voices) {
            Member member = voice.getMember();
            if (member.equals(user)) {
                isUser = true;
            }
            voiceDtoList.add(new VoiceDto(voice.getVoiceId(), member.getProfileUrl(), member.getName(), voice.getTranscript(), voice.getVoiceUrl(), isUser));
        }
        return new FriendPhotoDetailDto(photoId,
                photo.getTitle(),
                photo.getPhotoDate(),
                emojiType,
                voiceDtoList
        );
    }
}
