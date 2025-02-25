package Jeans.Jeans.Photo.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedPhotoDto {
    private Long photoId;
    private String photoUrl;
    private Boolean isTeam;

    @Builder
    public FeedPhotoDto(Long photoId, String photoUrl, Boolean isTeam){
        this.photoId = photoId;
        this.photoUrl = photoUrl;
        this.isTeam = isTeam;
    }
}
