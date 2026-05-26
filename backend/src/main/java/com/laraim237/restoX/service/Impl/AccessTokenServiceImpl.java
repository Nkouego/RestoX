package com.laraim237.restoX.service.Impl;

import org.springframework.stereotype.Service;

import com.laraim237.restoX.common.Exception.OTPException;
import com.laraim237.restoX.common.utils.OtpUtils;
import com.laraim237.restoX.entity.AccessToken;
import com.laraim237.restoX.entity.User;
import com.laraim237.restoX.enums.TokenType;
import com.laraim237.restoX.repository.AccessTokenRepository;
import com.laraim237.restoX.service.AccessTokenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccessTokenServiceImpl implements AccessTokenService{

	private final AccessTokenRepository accessTokenRepository;

	@Override
	public AccessToken generate(User user, TokenType type, int expirationMinutes) {
		//supprime l'ancien token s'il existe
		accessTokenRepository.deleteByUserAndType(user, TokenType.REGISTRATION);
		
		//Genere un nouveau code
		AccessToken accessToken = OtpUtils.generateOTP(user, TokenType.REGISTRATION, 15);
		accessTokenRepository.save(accessToken);
		
		return accessToken;	
	}

	@Override
	public AccessToken validate(User user, TokenType type, String code) {
		//Trouver le TokenType actif
		AccessToken accessToken = accessTokenRepository.findByUserAndType(user, TokenType.PASSWORD_RESET)
									.orElseThrow(() -> new OTPException("Invalid or expired code"));
		
    	//Verifier l'expiration
		if(accessToken.isExpired()) {
			throw new OTPException("Expired code, please request a new one");
		}
		
    	//Verifier code
		if(code.equals(accessToken.getToken())) {
			throw new OTPException("Invalid code");
		}
			
		return accessToken;
	}

}
