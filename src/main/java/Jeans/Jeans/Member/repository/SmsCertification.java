package Jeans.Jeans.Member.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class SmsCertification {
    private final String PREFIX = "sms:";  // key 값이 중복되지 않도록 상수 선언
    private final int LIMIT_TIME = 5 * 60;  // 인증번호 유효 시간

    private final StringRedisTemplate stringRedisTemplate;

    // Redis에 저장
    public void createSmsCertification(String phone, String certificationNumber) {
        stringRedisTemplate.opsForValue()
                .set(PREFIX + phone, certificationNumber, Duration.ofSeconds(LIMIT_TIME));
    }

    // Redis에 해당 전화번호로 저장된 인증번호가 존재하는지 확인
    public Boolean hasKey(String phone) {
        return stringRedisTemplate.hasKey(PREFIX + phone);
    }

    // 전화번호에 해당하는 인증번호 불러오기
    public String getSmsCertification(String phone) {
        return stringRedisTemplate.opsForValue().get(PREFIX + phone);
    }

    // 인증 완료 시 Redis에서 인증번호 삭제
    public void deleteSmsCertification(String phone) {
        stringRedisTemplate.delete(PREFIX + phone);
    }
}
