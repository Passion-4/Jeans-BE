package Jeans.Jeans.Photo.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoiceMessageResDto {
    private String voiceUrl;

    public VoiceMessageResDto(String voiceUrl){
        this.voiceUrl = voiceUrl;
    }
}
