package Jeans.Jeans.Photo.dto;

import Jeans.Jeans.Voice.dto.VoiceDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendPhotoDetailDto {
    private Long photoId;
    private String title;
    private LocalDate date;
    private Integer emojiType;
    private List<VoiceDto> voiceList;

    @Builder
    public FriendPhotoDetailDto(Long photoId, String title, LocalDate date, Integer emojiType, List<VoiceDto> voiceList){
        this.photoId = photoId;
        this.title = title;
        this.date = date;
        this.emojiType = emojiType;
        this.voiceList = voiceList;
    }
}
