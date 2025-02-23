package Jeans.Jeans.BasicEdit.service;

import Jeans.Jeans.BasicEdit.domain.BasicEdit;
import Jeans.Jeans.BasicEdit.respository.BasicEditRepository;
import Jeans.Jeans.Member.domain.Member;
import Jeans.Jeans.Member.repository.MemberRepository;
import Jeans.Jeans.Member.service.MemberService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service

public class BasicEditService {

    private final BasicEditRepository basicEditRepository;
    private final MemberService memberService;
    private final RestTemplate restTemplate;
    private final String FASTAPI_URL = "http://127.0.0.1:8000/basic_edit/";

    @Autowired
    public BasicEditService(BasicEditRepository basicEditRepository, MemberService memberService, RestTemplate restTemplate) {
        this.basicEditRepository = basicEditRepository;
        this.memberService = memberService;  // ✅ 올바르게 주입됨
        this.restTemplate = restTemplate;

    }
    // FastAPI에 이미지 전송하고 URL 받아오기
    private String sendImageToFastAPI(MultipartFile file, Boolean edit1, Boolean edit2, Boolean edit3) throws Exception {
        // HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // 요청 바디 생성
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };

        body.add("file", resource);
        body.add("edit1", edit1);
        body.add("edit2", edit2);
        body.add("edit3", edit3);
        // HTTP 요청 전송
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                FASTAPI_URL, HttpMethod.POST, requestEntity, Map.class);
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return (String) response.getBody().get("imageUrl");
        } else {
            throw new Exception("FastAPI에서 이미지 URL을 가져오는 데 실패했습니다.");
        }
    }

    // 첫 번째 보정값 설정 및 FastAPI 요청
    @Transactional
    public String updateFirstEdit(Long memberId, Boolean edit1, MultipartFile file) throws Exception {
        Member member = memberService.getMemberById(memberId);
        BasicEdit basicEdit = basicEditRepository.findByMember(member);

        if (basicEdit == null) {
            basicEdit = BasicEdit.builder()
                    .member(member)
                    .edit1(edit1)
                    .edit2(false)
                    .edit3(false)
                    .build();
        } else {
            basicEdit.setEdit1(edit1);
        }
        basicEditRepository.save(basicEdit);

        // FastAPI 호출 후 보정된 이미지 URL 반환
        return sendImageToFastAPI(file, edit1, false, false);
    }

    // 두 번째 보정값 설정 및 FastAPI 요청
    @Transactional
    public String updateSecondEdit(Long memberId, Boolean edit2, MultipartFile file) throws Exception {
        Member member = memberService.getMemberById(memberId);
        BasicEdit basicEdit = basicEditRepository.findByMember(member);

        if (basicEdit == null) {
            throw new IllegalArgumentException("첫 번째 보정을 먼저 설정해야 합니다.");
        }

        basicEdit.setEdit2(edit2);
        basicEditRepository.save(basicEdit);

        // FastAPI 호출 후 보정된 이미지 URL 반환
        return sendImageToFastAPI(file, basicEdit.getEdit1(), edit2, false);
    }

    // 세 번째 보정값 설정 및 FastAPI 요청
    @Transactional
    public String updateThirdEdit(Long memberId, Boolean edit3, MultipartFile file) throws Exception {
        Member member = memberService.getMemberById(memberId);
        BasicEdit basicEdit = basicEditRepository.findByMember(member);

        if (basicEdit == null) {
            throw new IllegalArgumentException("첫 번째와 두 번째 보정을 먼저 설정해야 합니다.");
        }

        basicEdit.setEdit3(edit3);
        basicEditRepository.save(basicEdit);

        // FastAPI 호출 후 최종 보정된 이미지 URL 반환
        return sendImageToFastAPI(file, basicEdit.getEdit1(), basicEdit.getEdit2(), edit3);
    }
}





