package Jeans.Jeans.Member.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomDto {
    private Long memberId;
    private Long teamId;
    private String name;
    private String imageUrl;
    private String nickname;
    private LocalDateTime createdDate;

    @Builder
    public ChatRoomDto(Long memberId, Long teamId, String name, String imageUrl, String nickname, LocalDateTime createdDate){
        this.memberId = memberId;
        this.teamId = teamId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.nickname = nickname;
        this.createdDate = createdDate;
    }
}
