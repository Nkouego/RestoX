package com.laraim237.restoX.modules.auth.dto;

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
		String password,
		
		@NotBlank(message = "Restaurant's name is required")
		String restaurantName
	) {}
	
	public static record VerifyEmailRequest(
		@NotBlank(message = "Email is required")
		@Email(message = "Email is not valid")
		String email,
		
		@NotBlank(message = "Code is required")
		@Size(min = 6, max=6, message = "Code must be 6 characters")
		String code
	) {}
	
	@Builder
	@Data
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class AuthResponse {
		private String message;
		private String token;
		private String refreshToken;
	}


}
