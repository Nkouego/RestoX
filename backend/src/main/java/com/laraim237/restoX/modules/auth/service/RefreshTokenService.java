package com.laraim237.restoX.modules.auth.service;

import com.laraim237.restoX.modules.auth.entity.RefreshToken;
import com.laraim237.restoX.modules.user.User;

public interface RefreshTokenService {

	String generateRefreshToken(User user);

	void revokeAllTokens(Long userId);

	RefreshToken validateRefreshToken(String refreshToken);

}
