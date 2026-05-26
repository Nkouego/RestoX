package com.laraim237.restoX.common.utils;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

import com.laraim237.restoX.entity.AccessToken;
import com.laraim237.restoX.entity.User;
import com.laraim237.restoX.enums.TokenType;


public class OtpUtils {
	
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	public static AccessToken generateOTP(User user, TokenType tokenType, int expiryMinutes) {
		String token = String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
		AccessToken accessToken = new AccessToken(token, user, expiryMinutes, tokenType);
	
		return accessToken;
	}

}
