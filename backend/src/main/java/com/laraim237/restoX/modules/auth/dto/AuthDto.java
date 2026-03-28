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
		@NotBlank(message = "The first name is required")
		@Size(min = 3, message = "First name must be at least 3 characters")
		String firstName,
		
		@NotBlank(message = "The Last name is required")
		@Size(min = 3, message = "Last name must be at least 3 characters")
		String lastName,
		
		@NotBlank(message = "The email is required")
		@Email(message = "Email is not valid")
		String email,
		
		@NotBlank(message = "The password is required")
		@Size(min = 8, message = "Password be at least 8 characters")
		String password,
		
		@NotBlank(message = "The Restaurant's name is required")
		String restaurantName
	) {}
	
	@Builder
	@Data
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class AuthResponse {
		private String message;
		private String token;
	}


}
