package Jeans.Jeans.Photo.service;

import Jeans.Jeans.Member.domain.Member;
import Jeans.Jeans.Member.repository.MemberRepository;
import Jeans.Jeans.MemberPhoto.domain.MemberPhoto;
import Jeans.Jeans.MemberPhoto.repository.MemberPhotoRepository;
import Jeans.Jeans.Photo.domain.Photo;
import Jeans.Jeans.Photo.dto.FriendShareReqDto;
import Jeans.Jeans.Photo.dto.PhotoShareResDto;
import Jeans.Jeans.Photo.repository.PhotoRepository;
import Jeans.Jeans.PhotoTag.domain.PhotoTag;
import Jeans.Jeans.PhotoTag.repository.PhotoTagRepository;
import Jeans.Jeans.Tag.domain.Tag;
import Jeans.Jeans.Tag.repository.TagRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PhotoService {
    private final PhotoRepository photoRepository;
    private final MemberPhotoRepository memberPhotoRepository;
    private final MemberRepository memberRepository;
    private final TagRepository tagRepository;
    private final PhotoTagRepository photoTagRepository;

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
}
