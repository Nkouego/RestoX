package com.laraim237.restoX.dto;

import java.time.Instant;

import org.springframework.web.multipart.MultipartFile;

import com.laraim237.restoX.enums.RestaurantRole;
import com.laraim237.restoX.enums.RestaurantStatus;
import com.laraim237.restoX.enums.StaffStatus;
import com.laraim237.restoX.enums.SystemRole;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserDto {
	
	public record UserProfileResponse(
			String id,
			String firstName,
			String lastName,
			String email,
			String pictureUrl,
			Instant createdAt,
			RestaurantStatus restaurantStatus,
			SystemRole systemRole,
			RestaurantRole role,
			StaffStatus staffStatus,
			Instant assignedAt
			) {} 

	public record UpdateProfileRequest(
			String firstName,
			String lastName,
			MultipartFile picture
			) {}
	
	public record ChangePasswordRequest(
		    @NotBlank(message = "Code is required")
		    @Size(min = 6, max = 6)
		    String code,

		    @NotBlank(message = "Password is required")
		    @Size(min = 8, message = "Password must be at least 8 characters")
		    String newPassword
		) {}
	
}
