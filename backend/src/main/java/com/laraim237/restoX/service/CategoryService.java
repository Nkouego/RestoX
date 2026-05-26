package com.laraim237.restoX.service;

import java.io.IOException;
import java.util.List;

import com.laraim237.restoX.dto.CategoryDto.AddCategoryRequest;
import com.laraim237.restoX.dto.CategoryDto.CategoryResponse;
import com.laraim237.restoX.dto.CategoryDto.UpdateCategoryRequest;


public interface CategoryService {

	List<CategoryResponse> listCategories(String restaurantId);

	CategoryResponse createCategory(String restaurantId, AddCategoryRequest request) throws IOException;

	CategoryResponse updateCategory( UpdateCategoryRequest request, String restaurantId, String categoryId) throws IOException;

	void deleteCategory(String restaurantId, String categoryId) throws IOException;

}
