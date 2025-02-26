package Jeans.Jeans.Photo.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EditResponseDto {
    private String originalUrl;
    private String editedUrl;

    @Builder
    public EditResponseDto(String originalUrl, String editedUrl){
        this.originalUrl = originalUrl;
        this.editedUrl = editedUrl;
    }
}
