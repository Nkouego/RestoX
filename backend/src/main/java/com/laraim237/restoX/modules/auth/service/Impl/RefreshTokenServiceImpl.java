package com.laraim237.restoX.modules.auth.service.Impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.stereotype.Service;

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
				.expiryDate(Instant.now().plus(7, ChronoUnit.DAYS))
				.revoked(false)
				.build();
		refreshTokenRepository.save(refreshToken);
		
		return refreshToken.getToken();
	}

}
