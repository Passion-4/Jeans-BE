package Jeans.Jeans.Photo.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BestPhotoResDto {
    private String faceUrl1;
    private String faceUrl2;
    private String faceUrl3;
    private String faceUrl4;

    @Builder
    public BestPhotoResDto(String faceUrl1, String faceUrl2, String faceUrl3, String faceUrl4){
        this.faceUrl1 = faceUrl1;
        this.faceUrl2 = faceUrl2;
        this.faceUrl3 = faceUrl3;
        this.faceUrl4 = faceUrl4;
    }
}
