package Jeans.Jeans.Photo.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class photoEditDto {
    private String originalImageUrl;
    private String editedImageUrl;
    private String editedImageUrl1;   // face_slimming용
    private String editedImageUrl2;

    @Builder
    public photoEditDto(String originalImageUrl, String editedImageUrl, String editedImageUrl1, String editedImageUrl2) {
        this.originalImageUrl = originalImageUrl;
        this.editedImageUrl = editedImageUrl;
        this.editedImageUrl1 = editedImageUrl1;
        this.editedImageUrl2 = editedImageUrl2;
    }
}
