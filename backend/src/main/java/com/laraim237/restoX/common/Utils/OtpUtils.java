package com.laraim237.restoX.common.Utils;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

import com.laraim237.restoX.entity.AccessToken;
import com.laraim237.restoX.entity.User;
import com.laraim237.restoX.enums.TokenType;


public class OtpUtils {
	
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();
	private static final int OTP_EXPIRY_MINUTES = 15;

	public static AccessToken generateAndSaveOTP(User user, TokenType tokenType) {
		String token = String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
		AccessToken accessToken = new AccessToken(token, user, OTP_EXPIRY_MINUTES, tokenType);
	
		return accessToken;
	}

}
