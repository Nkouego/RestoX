package com.laraim237.restoX.modules.auth.service;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;

import com.laraim237.restoX.modules.auth.dto.AuthDto.AuthResponse;
import com.laraim237.restoX.modules.auth.dto.AuthDto.ForgotPasswordRequest;
import com.laraim237.restoX.modules.auth.dto.AuthDto.LoginRequest;
import com.laraim237.restoX.modules.auth.dto.AuthDto.RefreshTokenRequest;
import com.laraim237.restoX.modules.auth.dto.AuthDto.RegisterRequest;
import com.laraim237.restoX.modules.auth.dto.AuthDto.ResendCodeRequest;
import com.laraim237.restoX.modules.auth.dto.AuthDto.ResetPasswordRequest;
import com.laraim237.restoX.modules.auth.dto.AuthDto.VerifyEmailRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

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
