package Jeans.Jeans.Photo.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmoticonDto {
    private Integer emojiType;
    private String name;
    private String profileUrl;

    @Builder
    public EmoticonDto(Integer emojiType, String name, String profileUrl){
        this.emojiType = emojiType;
        this.name = name;
        this.profileUrl = profileUrl;
    }
}
