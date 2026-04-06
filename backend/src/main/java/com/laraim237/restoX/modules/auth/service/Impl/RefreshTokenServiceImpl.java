package com.laraim237.restoX.modules.auth.service.Impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.laraim237.restoX.common.Exception.RefreshTokenException;
import com.laraim237.restoX.modules.auth.entity.RefreshToken;
import com.laraim237.restoX.modules.auth.repository.RefreshTokenRepository;
import com.laraim237.restoX.modules.auth.service.RefreshTokenService;
import com.laraim237.restoX.modules.user.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
	private final RefreshTokenRepository refreshTokenRepository;

	@Override
	public String generateRefreshToken(User user) {
		//revoquer tous les anciens tokens
		refreshTokenRepository.revokeAllByUserId(user.getId());
		
		//genere un nouveau refreshToken
		RefreshToken refreshToken = RefreshToken.builder()
				.user(user)
				.token(UUID.randomUUID().toString())
				.expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
				.revoked(false)
				.build();
		refreshTokenRepository.save(refreshToken);
		
		return refreshToken.getToken();
	}

	@Override
	public void revokeAllTokens(Long userId) {
		refreshTokenRepository.revokeAllByUserId(userId);	
	}

	@Override
	public RefreshToken validateRefreshToken(String refreshToken) {
	//On recherche le refreshToken en BD
	RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
						 .orElseThrow(() -> new RefreshTokenException("Refresh token not found"));
	
	//On verifie si le refresh token n'est pas revoqué
	if(token.isRevoked()) {
		throw new RefreshTokenException("Refresh token has been revoked");
	}
	
	//On verifie si le refresh token n'est pas expiré
	if(token.isExpired()) {
		throw new RefreshTokenException("Refresh token has been revoked");
	}
	
		return token;
	}

}
