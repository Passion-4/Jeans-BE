package Jeans.Jeans.Member.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BasicEditValueSaveResDto {
    private String imageUrl1;
    private String imageUrl2;

    public BasicEditValueSaveResDto(String imageUrl1, String imageUrl2){
        this.imageUrl1 = imageUrl1;
        this.imageUrl2 = imageUrl2;
    }
}
