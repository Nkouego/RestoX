package com.laraim237.restoX.modules.auth.service;

import com.laraim237.restoX.modules.auth.dto.AuthDto;
import com.laraim237.restoX.modules.auth.dto.AuthDto.AuthResponse;
import com.laraim237.restoX.modules.auth.dto.AuthDto.RegisterRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

public interface Authservice {

	AuthResponse register(RegisterRequest request, HttpServletRequest httpRequest);

}
