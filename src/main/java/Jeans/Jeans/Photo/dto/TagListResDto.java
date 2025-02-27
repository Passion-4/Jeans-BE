package Jeans.Jeans.Photo.dto;

import Jeans.Jeans.Tag.domain.Tag;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TagListResDto {
    private Long photoId;
    private List<String> tagList;

    @Builder
    public TagListResDto(Long photoId, List<String> tagList){
        this.photoId = photoId;
        this.tagList = tagList;
    }
}
