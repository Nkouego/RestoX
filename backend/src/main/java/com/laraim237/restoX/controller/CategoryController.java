package com.laraim237.restoX.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laraim237.restoX.dto.CategoryDto.AddCategoryRequest;
import com.laraim237.restoX.dto.CategoryDto.CategoryResponse;
import com.laraim237.restoX.dto.CategoryDto.UpdateCategoryRequest;
import com.laraim237.restoX.service.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/menu/categories")
@RequiredArgsConstructor
public class CategoryController {
	
	private final CategoryService service;
	
	@GetMapping
    public ResponseEntity<List<CategoryResponse>> list(@PathVariable String restaurantId) {
        return ResponseEntity.ok(service.listCategories(restaurantId));
    }
	
	@PostMapping
	public ResponseEntity<CategoryResponse> create(@PathVariable String restaurantId,@Valid @RequestBody AddCategoryRequest request) throws IOException{
		return ResponseEntity.status(HttpStatus.CREATED).body(service.createCategory(restaurantId, request));
	}
	
	@PutMapping("/{categoryId}")
	public ResponseEntity<CategoryResponse> update(@PathVariable String restaurantId,
			                                    @PathVariable String categoryId,
			                                    @RequestBody @Valid UpdateCategoryRequest request) throws IOException{
		return ResponseEntity.ok(service.updateCategory(request, restaurantId, categoryId));
	}

	public ResponseEntity<Void> delete(@PathVariable String restaurantId,
                                       @PathVariable String categoryId) throws IOException{
		service.deleteCategory(restaurantId, categoryId);
		return ResponseEntity.noContent().build();
	}
}
