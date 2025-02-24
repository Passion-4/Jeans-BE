package Jeans.Jeans.Photo.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhotoShareResDto {
    private String photoUrl;

    @Builder
    public PhotoShareResDto(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}