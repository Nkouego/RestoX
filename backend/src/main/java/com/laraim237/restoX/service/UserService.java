package com.laraim237.restoX.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;

import com.laraim237.restoX.dto.UserDto.ChangePasswordRequest;
import com.laraim237.restoX.dto.UserDto.UpdateProfileRequest;
import com.laraim237.restoX.dto.UserDto.UserProfileResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface UserService {

	UserProfileResponse getMyProfile(Authentication authentication);
	
	UserProfileResponse updateProfile(UpdateProfileRequest request, Authentication authentication) throws IOException;

	void requestChangePassword(Authentication authentication, HttpServletRequest httpRequest);
	
	void confirmChangePassword(ChangePasswordRequest request, Authentication authentication, HttpServletRequest httpRequest);
}
