package Jeans.Jeans.Photo.dto;

import Jeans.Jeans.Voice.dto.VoiceDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamPhotoDetailDto {
    private Long photoId;
    private String title;
    private LocalDate date;
    private List<Integer> emojiTypeList;
    private List<VoiceDto> voiceList;

    public TeamPhotoDetailDto(Long photoId, String title, LocalDate date, List<Integer> emojiTypeList, List<VoiceDto> voiceList){
        this.photoId = photoId;
        this.title = title;
        this.date = date;
        this.emojiTypeList = emojiTypeList;
        this.voiceList = voiceList;
    }
}
