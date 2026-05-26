package com.laraim237.restoX.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laraim237.restoX.dto.UserDto.ChangePasswordRequest;
import com.laraim237.restoX.dto.UserDto.UpdateProfileRequest;
import com.laraim237.restoX.dto.UserDto.UserProfileResponse;
import com.laraim237.restoX.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/")
public class UserController {
	
	private final UserService userService;
	
	@GetMapping("/me")
	public ResponseEntity<UserProfileResponse> getMyProfile(Authentication authentication){
		return ResponseEntity.ok(userService.getMyProfile(authentication));
	}
	
	@PutMapping("/me")
	public ResponseEntity<UserProfileResponse> updateProfile(
	        @ModelAttribute UpdateProfileRequest request,
	        Authentication authentication) throws IOException {
	    return ResponseEntity.ok(userService.updateProfile(request, authentication));
	}
	
	public ResponseEntity<Void> requestChangePassword(
			Authentication authentication,
			HttpServletRequest httpRequest){
		userService.requestChangePassword(authentication, httpRequest);
		return ResponseEntity.noContent().build();
	}
	
	public ResponseEntity<Void> confirmChangePassword(
			ChangePasswordRequest request,
			Authentication authentication,
			HttpServletRequest httpRequest){
		userService.confirmChangePassword(request, authentication, httpRequest);
		return ResponseEntity.noContent().build();
	}
	

}
