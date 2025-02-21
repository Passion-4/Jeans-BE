package Jeans.Jeans.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // File
    CONVERT_MULTIPARTFILE_ERROR(HttpStatus.BAD_REQUEST, "MultipartFile을 File로 전환하는 것에 실패했습니다."),
    NON_LOGIN(HttpStatus.UNAUTHORIZED, "로그인 후 이용 가능합니다.");

    private final HttpStatus status;
    private final String message;
}
