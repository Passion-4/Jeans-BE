package Jeans.Jeans.Photo.controller;

import Jeans.Jeans.Member.domain.Member;
import Jeans.Jeans.Member.service.MemberService;
import Jeans.Jeans.Photo.dto.photoEditDto;
import Jeans.Jeans.global.config.FastApiConfig;
import Jeans.Jeans.global.service.S3Uploader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/photo")
public class PhotoEditController {
    private final MemberService memberService;
    private final S3Uploader s3Uploader;
    private final RestTemplate restTemplate = new RestTemplate();
    private final FastApiConfig fastApiConfig;
    // FastAPI 서버 URL
    @Value("${fastapi.url}")
    private String fastApiUrl;

    private photoEditDto processImage(String filterEndpoint, MultipartFile image) throws IOException {

        String originalUrl = s3Uploader.upload(image, "original-images");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", image.getResource());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                fastApiUrl + filterEndpoint, requestEntity, String.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            if (filterEndpoint.equals("/upload/face_slimming")) {
                String url1 = jsonNode.get("editedImageUrl1").asText();
                String url2 = jsonNode.get("editedImageUrl2").asText();

                return photoEditDto.builder()
                        .originalImageUrl(originalUrl)
                        .editedImageUrl1(url1)
                        .editedImageUrl2(url2)
                        .build();
            } else {
                String editedUrl = jsonNode.get("editedImageUrl").asText();
                String finalEditedUrl = fastApiConfig.getFastApiUrl() + editedUrl;
                return photoEditDto.builder()
                        .originalImageUrl(originalUrl)
                        .editedImageUrl(finalEditedUrl)
                        .build();
            }
        }

        throw new RuntimeException("FastAPI 이미지 보정 실패");
    }

    @PostMapping(value = "/young", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public photoEditDto photoYoung(@RequestHeader("Authorization") String token,
                                       @RequestPart("image") MultipartFile image) throws IOException {
        Member user = memberService.getLoginMember();
        return processImage("/upload/youth", image);
    }

    @PostMapping(value = "/volume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public photoEditDto photoVolume(@RequestHeader("Authorization") String token,
                                        @RequestPart("image") MultipartFile image) throws IOException {
        Member user = memberService.getLoginMember();
        return processImage("/upload/hair", image);
    }

    @PostMapping(value = "/face_slim", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public photoEditDto photoFaceSlim(@RequestHeader("Authorization") String token,
                                          @RequestPart("image") MultipartFile image) throws IOException {
        Member user = memberService.getLoginMember();
        return processImage("/upload/face_slimming", image);
    }



}
