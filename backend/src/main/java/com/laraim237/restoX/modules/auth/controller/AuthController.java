package com.laraim237.restoX.modules.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laraim237.restoX.modules.auth.dto.AuthDto.AuthResponse;
import com.laraim237.restoX.modules.auth.dto.AuthDto.ForgotPasswordRequest;
import com.laraim237.restoX.modules.auth.dto.AuthDto.LoginRequest;
import com.laraim237.restoX.modules.auth.dto.AuthDto.RegisterRequest;
import com.laraim237.restoX.modules.auth.dto.AuthDto.ResendCodeRequest;
import com.laraim237.restoX.modules.auth.dto.AuthDto.ResetPasswordRequest;
import com.laraim237.restoX.modules.auth.dto.AuthDto.VerifyEmailRequest;
import com.laraim237.restoX.modules.auth.service.Authservice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
	
	private final Authservice authService;
	
	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request, HttpServletRequest httpRequest){
		return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request, httpRequest));	
	}
	
	@PostMapping("/verify-email")
	public ResponseEntity<AuthResponse> verifyEmail(@Valid @RequestBody VerifyEmailRequest request, HttpServletRequest httpRequest){
		return ResponseEntity.ok(authService.verifyEmail(request, httpRequest));	
	}
	
	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest){
		return ResponseEntity.ok(authService.login(request, httpRequest));	
	}
	@PostMapping("/forgot-password")
	public ResponseEntity<AuthResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request, HttpServletRequest httpRequest){
		return ResponseEntity.ok(authService.forgotPassword(request, httpRequest));	
	}
	@PostMapping("/reset-password")
	public ResponseEntity<AuthResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request, HttpServletRequest httpRequest){
		return ResponseEntity.ok(authService.resetPassword(request, httpRequest));	
	}
	
	@PostMapping("/resend-code")
	public ResponseEntity<AuthResponse> resendCode(@Valid @RequestBody ResendCodeRequest request, HttpServletRequest httpRequest){
		return ResponseEntity.ok(authService.resendCode(request, httpRequest));	
	}
	
	@PostMapping("/logout")
	public ResponseEntity<AuthResponse> logout(Authentication authentication, HttpServletRequest httpRequest){
		return ResponseEntity.ok(authService.logout(authentication, httpRequest));	
	}

}
