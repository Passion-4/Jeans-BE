package Jeans.Jeans.Voice.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoiceDto {
    private Long voiceId;
    private String profileUrl;
    private String name;
    private String transcript;
    private String voiceUrl;
    private Boolean isUser;

    @Builder
    public VoiceDto(Long voiceId, String profileUrl, String name, String transcript, String voiceUrl, Boolean isUser){
        this.voiceId = voiceId;
        this.profileUrl = profileUrl;
        this.name = name;
        this.transcript = transcript;
        this.voiceUrl = voiceUrl;
        this.isUser = isUser;
    }
}
