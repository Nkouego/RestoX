package com.laraim237.restoX.dto;

import java.time.Instant;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public class CategoryDto {
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Builder
	public record CategoryResponse(
		String id,
	    String name,
	    String description,
	    boolean active,
	    String imageUrl,
	    Instant createdAt,
	    Instant updatedAt
			) {}
	
	public record AddCategoryRequest(
		@NotBlank(message = "Category name is required")
        @Size(max = 100, message = "Category name too long max 100 length")
        String name,

        @NotBlank(message = "Category description is required")
        @Size(max = 255, message = "category too long max 255 length")
        String description,

        Integer position,
         
        MultipartFile image
			) {}
	
	public record UpdateCategoryRequest(
			String name,
		
			String description,
			
			Integer position,
			
			MultipartFile image
			) {}

}
