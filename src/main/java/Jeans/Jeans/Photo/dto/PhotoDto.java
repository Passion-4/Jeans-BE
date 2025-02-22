package Jeans.Jeans.Photo.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhotoDto {
    private Long photoId;
    private String photoUrl;

    @Builder
    public PhotoDto(Long photoId, String photoUrl){
        this.photoId = photoId;
        this.photoUrl = photoUrl;
    }
}
