package Jeans.Jeans.Photo.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhotoShareResDto {
    private String photoUrl;
    private String date;
    private String title;
    private List<String> tags;

    @Builder
    public PhotoShareResDto(String photoUrl, String date, String title, List<String> tags) {
        this.photoUrl = photoUrl;
        this.date = date;
        this.title = title;
        this.tags = tags;
    }
}