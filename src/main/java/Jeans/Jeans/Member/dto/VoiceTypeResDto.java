package Jeans.Jeans.Member.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoiceTypeResDto {
    private Long voiceType;

    @Builder
    public VoiceTypeResDto(Long voiceType){
        this.voiceType = voiceType;
    }
}
