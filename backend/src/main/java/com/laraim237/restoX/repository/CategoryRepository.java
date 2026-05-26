package com.laraim237.restoX.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.laraim237.restoX.dto.CategoryDto.CategoryResponse;
import com.laraim237.restoX.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, String> {

	List<Category> findByRestaurantId(String restaurantId);

	Optional<Category> findByIdAndRestaurant_Id(String categoryId, String restaurantId);

}
