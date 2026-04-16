package com.laraim237.restoX.service;

import com.laraim237.restoX.entity.RefreshToken;
import com.laraim237.restoX.entity.User;

public interface RefreshTokenService {

	String generateRefreshToken(User user);

	void revokeAllTokens(Long userId);

	RefreshToken validateRefreshToken(String refreshToken);

}
