package Jeans.Jeans.Member.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileResponseDto {
    private String name;
    private String profileUrl;
    private String birthday;
    private String phone;

    @Builder
    public ProfileResponseDto(String name, String profileUrl, String birthday, String phone){
        this.name = name;
        this.profileUrl = profileUrl;
        this.birthday = birthday;
        this.phone = phone;
    }
}
