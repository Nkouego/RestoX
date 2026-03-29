package com.laraim237.restoX.modules.auth.service;

import org.jspecify.annotations.Nullable;

import com.laraim237.restoX.modules.auth.dto.AuthDto.AuthResponse;
import com.laraim237.restoX.modules.auth.dto.AuthDto.LoginRequest;
import com.laraim237.restoX.modules.auth.dto.AuthDto.RegisterRequest;
import com.laraim237.restoX.modules.auth.dto.AuthDto.VerifyEmailRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

public interface Authservice {

	AuthResponse register(RegisterRequest request, HttpServletRequest httpRequest);

	AuthResponse verifyEmail(VerifyEmailRequest request, HttpServletRequest httpRequest);

	AuthResponse login(LoginRequest request, HttpServletRequest httpRequest);

}
