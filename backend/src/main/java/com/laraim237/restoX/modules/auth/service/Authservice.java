package com.laraim237.restoX.modules.auth.service;

import com.laraim237.restoX.modules.auth.dto.AuthDto.AuthResponse;
import com.laraim237.restoX.modules.auth.dto.AuthDto.RegisterRequest;
import com.laraim237.restoX.modules.auth.dto.AuthDto.VerifyEmailRequest;

import jakarta.servlet.http.HttpServletRequest;

public interface Authservice {

	AuthResponse register(RegisterRequest request, HttpServletRequest httpRequest);

	AuthResponse verifyEmail(VerifyEmailRequest request, HttpServletRequest httpRequest);

}
