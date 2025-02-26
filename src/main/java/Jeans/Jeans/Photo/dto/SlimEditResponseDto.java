package Jeans.Jeans.Photo.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SlimEditResponseDto {
    private String originalUrl;
    private String editedUrl1;
    private String editedUrl2;

    @Builder
    public SlimEditResponseDto(String originalUrl, String editedUrl1, String editedUrl2){
        this.originalUrl = originalUrl;
        this.editedUrl1 = editedUrl1;
        this.editedUrl2 = editedUrl2;
    }
}
