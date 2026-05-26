package com.laraim237.restoX.service.Impl;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.laraim237.restoX.common.Exception.ResourceNotFoundException;
import com.laraim237.restoX.dto.CategoryDto.AddCategoryRequest;
import com.laraim237.restoX.dto.CategoryDto.CategoryResponse;
import com.laraim237.restoX.dto.CategoryDto.UpdateCategoryRequest;
import com.laraim237.restoX.dto.StorageResult;
import com.laraim237.restoX.entity.Category;
import com.laraim237.restoX.entity.Restaurant;
import com.laraim237.restoX.mapper.CategoryMapper;
import com.laraim237.restoX.repository.CategoryRepository;
import com.laraim237.restoX.repository.RestaurantRepository;
import com.laraim237.restoX.service.CategoryService;
import com.laraim237.restoX.service.StorageService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService{

	private static final String folder = "restoX/restaurants/%s/categories";
	private final CategoryRepository categoryRepository;
	private final CategoryMapper categoryMapper;
	private final RestaurantRepository restaurantRepository;
	private final StorageService storageService;
	@Override
	public List<CategoryResponse> listCategories(String restaurantId) {
		return categoryRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
	}

	@Override
	public CategoryResponse createCategory(String restaurantId, AddCategoryRequest request) throws IOException {
		Restaurant restaurant = restaurantRepository.findById(restaurantId)
		        .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
		Category category = categoryMapper.toEntity(request);
		category.setRestaurant(restaurant);
		
		if(request.image() != null && !request.image().isEmpty() ) {
			String folderCategories = String.format(folder, restaurant.getId());
			StorageResult result = storageService.uploadFile(request.image(), folderCategories);
			category.setImageUrl(result.url());
			category.setImagePublicId(result.publicId());
		}
		return categoryMapper.toResponse(categoryRepository.save(category));
	}
	
	@Override
	public CategoryResponse updateCategory(UpdateCategoryRequest request, String restaurantId, String categoryId) throws IOException {
		Category category = categoryRepository.findByIdAndRestaurant_Id(categoryId, restaurantId)
	            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
		
		categoryMapper.updateCategory(request, category);
		
		 // 3. Gérer l'image
	    if (request.image() != null && !request.image().isEmpty()) {
	        // Supprimer l'ancienne image si elle existe
	        if (category.getImagePublicId() != null) {
	            storageService.delete(category.getImagePublicId());
	        }
	        // Uploader la nouvelle
	            String folderCategories = String.format(folder, restaurantId);
	            StorageResult storageResult = storageService.uploadFile(request.image(), folderCategories);
	            category.setImageUrl(storageResult.url());
	            category.setImagePublicId(storageResult.publicId());
	    }
	    
		Category updatedCategory = categoryRepository.save(category);
		
	    return categoryMapper.toResponse(updatedCategory);
	}

	@Override
	public void deleteCategory(String restaurantId, String categoryId) throws IOException {
		Category category = categoryRepository.findByIdAndRestaurant_Id(categoryId, restaurantId)
	            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
		
		if (category.getImagePublicId() != null) {
	        storageService.delete(category.getImagePublicId());
	    }
		
		category.setDeletedAt(Instant.now());
		categoryRepository.save(category);
	}
}
