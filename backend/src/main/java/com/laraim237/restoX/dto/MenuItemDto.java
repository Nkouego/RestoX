package com.laraim237.restoX.dto;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class MenuItemDto {
	
	public record AddMenuItemRequest(
		@NotBlank(message = "Item name is required")
        @Size(max = 100, message = "Item name too long max 100 length")
	    String name,
	    
	    @Size(max = 255, message = "Item description too long max 255 length")
	    String description,
	    
	    @NotNull
	    BigDecimal price,
	    
	    @NotNull
	    String categoryId,
	    
	    @NotNull(message = "Image is required")
	    MultipartFile image
	) {}

	public record MenuItemResponse(
		String id,
		String name,
		String description,
		String imageUrl,
		BigDecimal price,
		boolean available,
		String categoryId,
		String categoryName,
		Instant createdAt,
		Instant updatedAt
	) {}
	
	public record UpdateMenuItemRequest(
		    @Size(max = 100, message = "Item name too long max 100 length")
		    String name,
		    
		    @Size(max = 255, message = "Item description too long max 255 length")
		    String description,
		    
		    BigDecimal price,
		    
		    Boolean available,
		    
		    String categoryId,
		    
		    MultipartFile image
		) {}
	
	public record MenuItemFilterRequest(
			String categoryId,
			Boolean available,
			String search,
			int page,
			int size
	) {
		public MenuItemFilterRequest {
			if(page<0) page = 0;
			if(size<=0 || size>50) size = 10;
		}
		
			}

}
