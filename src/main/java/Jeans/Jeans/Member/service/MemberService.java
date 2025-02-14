package Jeans.Jeans.Member.service;

import Jeans.Jeans.Member.domain.Member;
import Jeans.Jeans.Member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;

    public String signUp(String name, LocalDate birthday, String phone, String password){
        if(existsByPhone(phone)) throw new RuntimeException(phone + "은 이미 존재하는 전화번호입니다.");

        memberRepository.save(
                Member.builder()
                        .name(name)
                        .birthday(birthday)
                        .phone(phone)
                        .password(encoder.encode(password))
                        .profileUrl(null)
                        .build()
        );
        return "회원가입이 완료되었습니다.";
    }

    // 회원 가입 시 입력한 전화번호를 가진 member 존재 여부 확인
    @Transactional(readOnly = true)
    public boolean existsByPhone(String phone){
        return memberRepository.existsByPhone(phone);
    }
}
