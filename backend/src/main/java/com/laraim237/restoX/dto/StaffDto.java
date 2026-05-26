package com.laraim237.restoX.dto;

import java.util.List;

import org.springframework.boot.context.properties.bind.DefaultValue;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.laraim237.restoX.enums.RestaurantRole;
import com.laraim237.restoX.enums.StaffStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public class StaffDto {
	
	@Builder(toBuilder = true)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record StaffResponse(
			String id,
			String message,
			String firstName,
			String lastName,
		    String email, 
		    boolean enabled,
		    StaffStatus status,
		    RestaurantRole role,
		    int page,
		    int size,
		    long totalElements,
		    int totalPages,
		    boolean last
		  	) {}
	

	public record AddStaffRequest(
			@NotBlank(message = "First name is required")
			@Size(min = 3, message = "First name must be at least 3 characters")
			String firstName,
			
			@NotBlank(message = "Last name is required")
			@Size(min = 3, message = "Last name must be at least 3 characters")
			String lastName,
			
			@NotBlank(message = "Email name is required")
			@Email(message = "Email is not valid")
			String email,
			
			@NotBlank(message = "Role is required")
			RestaurantRole role
			) {}
	
	public record UpdateStaffRequest(
			@Size(min = 3, message = "First name must be at least 3 characters")
			String firstName,
			
			@Size(min = 3, message = "Last name must be at least 3 characters")
			String lastName,
			
			@Email(message = "Email is not valid")
			String email,
			
			RestaurantRole role
			) {}
	
	public record ConfirmInvitationRequest(
			@NotBlank(message = "password is required")
			@Size(min = 8, message = "Password must be at least 8 characters")
			String password,
			
			@NotBlank(message = "token is required")
			String token
			) {}
	
	public static record ResendInviteRequest(
			@NotBlank(message = "Email is required")
			@Email(message = "Email is not valid")
			String email
			) {}
	
	public record StaffFilterRequest(
	    StaffStatus status,   
	    String search,        
	    @DefaultValue("0") int page,             
	    @DefaultValue("10") int size              
	) {
	    public StaffFilterRequest {
	        if (page < 0) page = 0;
	        if (size <= 0 || size > 50) size = 10;
	    }
	}
	
	public record PagedStaffResponse(
		    int page,
		    int size,
		    long totalElements,
		    int totalPages,
		    boolean last
		) {}
}
