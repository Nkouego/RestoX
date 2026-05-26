package com.laraim237.restoX.dto;

import java.time.Instant;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.laraim237.restoX.enums.RestaurantRole;
import com.laraim237.restoX.enums.RestaurantStatus;
import com.laraim237.restoX.enums.StaffStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

public class RestaurantDto {
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Builder
	public record RestaurantResponse(
		    String id,
		    String name,
		    String description,
		    String address,
		    String phone,
		    String logoUrl,
		    RestaurantStatus restaurantStatus,
		    RestaurantRole role,      
		    StaffStatus staffStatus,
		    Instant createdAt,
		    String message,
		    String token
		) {}
	
	public static record CreateRestaurantRequest(
		@NotBlank(message = "Restaurant name is required")
		String name,
		
		@NotBlank(message = "Restaurant description is required")
		String description,
		
		@NotBlank(message = "Restaurant address is required")
		String address,
		
		String logoUrl
		) {}
	
	public static record UpdateRestaurantRequest(
			String name,
			String description,
			String address,		
			String logoUrl
			) {}
	
	public static record SwitchRestaurantRequest(
			@NotBlank(message = "Restaurant id is required")
			String idRestaurant
			) {}
	
	
}
