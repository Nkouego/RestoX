package com.laraim237.restoX.service.Impl;

import java.net.http.HttpRequest;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.laraim237.restoX.common.utils.OtpUtils;
import com.laraim237.restoX.entity.AccessToken;
import com.laraim237.restoX.entity.User;
import com.laraim237.restoX.enums.AuditAction;
import com.laraim237.restoX.enums.TokenType;
import com.laraim237.restoX.repository.AccessTokenRepository;
import com.laraim237.restoX.repository.UserRepository;
import com.laraim237.restoX.service.AccessTokenService;
import com.laraim237.restoX.service.AuditService;
import com.laraim237.restoX.service.EmailService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordService {

	private final UserRepository userRepository;
	private final AccessTokenRepository accessTokenRepository;
	
	private final EmailService emailService;
	private final AuditService auditService;
	private final AccessTokenService accessTokenService;

	private final PasswordEncoder passwordEncoder;

	public void sendPasswordResetCode(User user, HttpServletRequest httpRequest) {
		
		 //On supprime l'ancien token si il existe 
		  accessTokenRepository.deleteByUserAndType(user, TokenType.PASSWORD_RESET);
		  
		  //Genere un nouveau code
		  AccessToken accessToken = OtpUtils.generateOTP(user, TokenType.PASSWORD_RESET, 15);
		  accessTokenRepository.save(accessToken);
		  
		  //Envoyer l'email
		  emailService.sendPasswordResetEmail(user, accessToken);
		  
		  //Audit
		  auditService.log(AuditAction.PASSWORD_RESET_REQUESTED, user.getId(), "User", null, null, null, user.getId(), httpRequest);
			  
	}
	 
	public void confirmPasswordReset(User user, String code, String newPassword, HttpServletRequest httpRequest) {
		//Valider le token d'access
		AccessToken accessToken = accessTokenService.validate(user, TokenType.PASSWORD_RESET, code);
		
		//change le mot de passe
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);
		
    	//supprimer le token utilisé
		accessTokenRepository.delete(accessToken);
		
		 //Audit
	    auditService.log(AuditAction.PASSWORD_RESET_CONFIRMED, user.getId(), "User", null, null, null, user.getId(), httpRequest);
	    
	}
}
