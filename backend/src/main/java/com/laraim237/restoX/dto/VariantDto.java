package com.laraim237.restoX.dto;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class VariantDto {

	public record AddVariantRequest(
			@NotBlank(message = "Variant name is required")
	        @Size(max = 100, message = "Variant name is too long")
			String name,
			
			@NotNull(message = "Variant image is required")
			MultipartFile image,
			
			@Size(max = 255, message = "Variant description is too long")
			String description,
			
			@NotNull(message = "Price is required")
			BigDecimal price,
			
			@NotNull(message = "MenuItem is required")
			String menuItemId
			) {}
	
	public record UpdateVariantRequest(
			String name,
			MultipartFile image,
			String description,
			BigDecimal price,
			Boolean available
			) {}
	
	public record VariantResponse(
	        String id,
	        String name,
	        String description,
	        String imageUrl,
	        BigDecimal price,
	        boolean available,
	        Instant createdAt,
	        Instant updatedAt
	    ) {}
}
