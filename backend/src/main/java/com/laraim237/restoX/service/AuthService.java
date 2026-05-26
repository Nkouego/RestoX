package com.laraim237.restoX.service;

import org.springframework.security.core.Authentication;

import com.laraim237.restoX.dto.AuthDto.AuthResponse;
import com.laraim237.restoX.dto.AuthDto.ForgotPasswordRequest;
import com.laraim237.restoX.dto.AuthDto.LoginRequest;
import com.laraim237.restoX.dto.AuthDto.RefreshTokenRequest;
import com.laraim237.restoX.dto.AuthDto.RegisterRequest;
import com.laraim237.restoX.dto.AuthDto.ResendCodeRequest;
import com.laraim237.restoX.dto.AuthDto.ResetPasswordRequest;
import com.laraim237.restoX.dto.AuthDto.VerifyEmailRequest;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

	AuthResponse register(RegisterRequest request, HttpServletRequest httpRequest);

	AuthResponse verifyEmail(VerifyEmailRequest request, HttpServletRequest httpRequest);

	AuthResponse login(LoginRequest request, HttpServletRequest httpRequest);

	AuthResponse resendCode(ResendCodeRequest request, HttpServletRequest httpRequest);
	
	AuthResponse forgotPassword(ForgotPasswordRequest request, HttpServletRequest httpRequest);
	
	AuthResponse resetPassword(ResetPasswordRequest request, HttpServletRequest httpRequest);

	AuthResponse logout(Authentication authentication, HttpServletRequest httpRequest);

	AuthResponse refreshToken(RefreshTokenRequest request, HttpServletRequest httpRequest);

}
