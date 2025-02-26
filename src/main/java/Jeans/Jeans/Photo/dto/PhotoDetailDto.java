package Jeans.Jeans.Photo.dto;

import Jeans.Jeans.Voice.dto.VoiceDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhotoDetailDto {
    private Long photoId;
    private String photoUrl;
    private String title;
    private LocalDate date;
    private Boolean isSharer;
    private List<Integer> emojiTypeList;
    private List<VoiceDto> voiceList;

    public PhotoDetailDto(Long photoId, String photoUrl, String title, LocalDate date, Boolean isSharer, List<Integer> emojiTypeList, List<VoiceDto> voiceList){
        this.photoId = photoId;
        this.photoUrl = photoUrl;
        this.title = title;
        this.date = date;
        this.isSharer = isSharer;
        this.emojiTypeList = emojiTypeList;
        this.voiceList = voiceList;
    }
}
