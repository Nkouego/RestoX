package com.laraim237.restoX.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

public class AuthDto {
	
	public static record RegisterRequest(
		@NotBlank(message = "First name is required")
		@Size(min = 3, message = "First name must be at least 3 characters")
		String firstName,
		
		@NotBlank(message = "Last name is required")
		@Size(min = 3, message = "Last name must be at least 3 characters")
		String lastName,
		
		@NotBlank(message = "Email is required")
		@Email(message = "Email is not valid")
		String email,
		
		@NotBlank(message = "Password is required")
		@Size(min = 8, message = "Password be at least 8 characters")
		String password
	) {}
	
	public static record VerifyEmailRequest(
		@NotBlank(message = "Email is required")
		@Email(message = "Email is not valid")
		String email,
		
		@NotBlank(message = "Code is required")
		@Size(min = 6, max=6, message = "Code must be 6 characters")
		String code
	) {}
	
	public static record LoginRequest(
			@NotBlank(message = "Email is required")
			@Email(message = "Email is not valid")
			String email,
			
			@NotBlank(message = "Password is required")
			String password
	) {}
	
	public static record ResendCodeRequest(
			@NotBlank(message = "Email is required")
			@Email(message = "Email is not valid")
			String email
			) {}
	
	public static record ForgotPasswordRequest(
			@Email(message = "Email is not valid")
			String email
			) {}
	
	public static record ResetPasswordRequest(
			@NotBlank(message = "Email is required")
			@Email(message = "Email is not valid")
			String email,
			
			@NotBlank(message = "Code is required")
			@Size(min = 6, max=6, message = "Code must be 6 characters")
			String code,
			
			@NotBlank(message = "Password is required")
			@Size(min = 8, message = "Password be at least 8 characters")
			String newPassword
			) {}
	
	public static record RefreshTokenRequest(
			@NotBlank(message = "Refresh token is required")
			String refreshToken
	) {}
	
	@Builder
	@Data
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class AuthResponse {
		private String id;
		private String firstName;
		private String lastName;
		private String email;
		private Boolean enabled;
		private String message;
		private String token;
		private String refreshToken;
		private Instant createdAt;
		private Instant updatedAt;
	}


}
