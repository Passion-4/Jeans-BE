package Jeans.Jeans.Member.service;

import Jeans.Jeans.Member.dto.VerificationReqDto;
import Jeans.Jeans.Member.repository.SmsCertification;
import lombok.RequiredArgsConstructor;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MessageService {
    @Value("${coolsms.api-key}")
    private String apiKey;

    @Value("${coolsms.api-secret}")
    private String apiSecret;

    @Value("${coolsms.from-number}")
    private String fromNumber;

    private final SmsCertification smsCertification;

    // 인증번호 전송하기
    public String sendSms(String phone) {
        Message coolsms = new Message(apiKey, apiSecret);

        // 랜덤한 인증 번호 생성
        String randomNum = createRandomNumber();
        System.out.println(randomNum);

        // 발신 정보 설정
        HashMap<String, String> params = makeParams(phone, randomNum);

        try {
            JSONObject obj = (JSONObject) coolsms.send(params);
            System.out.println(obj.toString());
        } catch (CoolsmsException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCode());
        }

        // DB에 발송한 인증번호 저장
        smsCertification.createSmsCertification(phone,randomNum);
        return "인증번호 문자 전송이 완료되었습니다.";
    }

    private String createRandomNumber() {
        Random rand = new Random();
        String randomNum = "";
        for (int i = 0; i < 4; i++) {
            String random = Integer.toString(rand.nextInt(10));
            randomNum += random;
        }
        return randomNum;
    }

    private HashMap<String, String> makeParams(String to, String randomNum) {
        HashMap<String, String> params = new HashMap<>();
        params.put("from", fromNumber);
        params.put("type", "SMS");
        params.put("app_version", "app 1.0");
        params.put("to", to);
        params.put("text", "[청바지] 본인확인 인증번호[" + randomNum+ "]를 화면에 입력해주세요");
        return params;
    }

    // 인증번호 일치 여부 확인
    public Boolean checkVerification(VerificationReqDto requestDto) {
        return !(smsCertification.hasKey(requestDto.getPhone()) &&
                smsCertification.getSmsCertification(requestDto.getPhone())
                        .equals(requestDto.getRandomNumber()));
    }

    // 인증 완료 시 DB에서 인증번호 삭제
    public void deleteSmsCertification(String phone) {
        smsCertification.deleteSmsCertification(phone);
    }
}
