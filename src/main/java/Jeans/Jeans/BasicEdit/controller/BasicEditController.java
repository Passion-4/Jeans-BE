package Jeans.Jeans.BasicEdit.controller;

import Jeans.Jeans.BasicEdit.domain.BasicEdit;
import Jeans.Jeans.BasicEdit.service.BasicEditService;
import Jeans.Jeans.Member.domain.Member;
import Jeans.Jeans.Member.service.MemberService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/mybasic/")
public class BasicEditController {
    private final BasicEditService basicEditService;
    private final MemberService memberService;

    public BasicEditController(BasicEditService basicEditService, MemberService memberService) {
        this.basicEditService = basicEditService;
        this.memberService = memberService;
    }

    // ✅ 첫 번째 보정값 설정 API (파일과 함께 edit1 전달)
    @PostMapping("/first/{memberId}")
    public ResponseEntity<Map<String, String>> setFirstEdit(
            @PathVariable("memberId") Long memberId,
            @RequestParam("file") MultipartFile file,  // ✅ 파일 추가
            @RequestParam("edit1") Boolean edit1) {    // ✅ edit1을 RequestParam으로 받음
        try {
            // FastAPI 요청 후 보정된 이미지 URL 반환
            String imageUrl = basicEditService.updateFirstEdit(memberId, edit1, file);

            // 응답 데이터 구성
            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", imageUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // ⬅️ 이 코드를 반드시 추가!
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // ✅ 두 번째 보정값 설정 API (파일과 함께 edit2 전달)
    @PostMapping("/second/{memberId}")
    public ResponseEntity<Map<String, String>> setSecondEdit(
            @PathVariable("memberId") Long memberId,
            @RequestParam("file") MultipartFile file,  // ✅ 파일 추가
            @RequestParam("edit2") Boolean edit2) {    // ✅ edit2를 RequestParam으로 받음
        try {
            // FastAPI 요청 후 보정된 이미지 URL 반환
            String imageUrl = basicEditService.updateSecondEdit(memberId, edit2, file);

            // 응답 데이터 구성
            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", imageUrl);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    // ✅ 세 번째 보정값 설정 API (파일과 함께 edit3 전달)
    @PostMapping("/third/{memberId}")
    public ResponseEntity<String> setThirdEdit(
            @PathVariable("memberId") Long memberId,
            @RequestParam("file") MultipartFile file,  // ✅ 파일 추가
            @RequestParam("edit3") Boolean edit3) {    // ✅ edit3를 RequestParam으로 받음
        try {
            // FastAPI 요청 후 보정된 이미지 URL 반환
            String imageUrl = basicEditService.updateThirdEdit(memberId, edit3, file);

            return ResponseEntity.ok("기본 보정 설정이 완료되었습니다. 최종 이미지 URL: " + imageUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("설정 저장 중 오류 발생: " + e.getMessage());
        }
    }
}
