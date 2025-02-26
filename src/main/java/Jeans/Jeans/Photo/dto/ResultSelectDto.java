package Jeans.Jeans.Photo.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResultSelectDto {
    private String photoUrl;

    @Builder
    public ResultSelectDto(String photoUrl){
        this.photoUrl = photoUrl;
    }
}
