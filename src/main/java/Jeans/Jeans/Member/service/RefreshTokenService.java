package Jeans.Jeans.Member.service;

import Jeans.Jeans.Member.domain.RefreshToken;
import Jeans.Jeans.Member.repository.RefreshTokenRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public void addRefreshToken(RefreshToken refreshToken){
        refreshTokenRepository.save(refreshToken);
    }

    @Transactional(readOnly = true)
    public RefreshToken findRefreshToken(String refreshTokenValue){
        return refreshTokenRepository.findByValue(refreshTokenValue)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 RefreshToken입니다."));
    }

    public void deleteRefreshToken(String refreshTokenValue){
        RefreshToken refreshToken = refreshTokenRepository.findByValue(refreshTokenValue)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 RefreshToken입니다."));
        refreshTokenRepository.delete(refreshToken);
    }
}
