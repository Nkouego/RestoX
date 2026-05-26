package com.laraim237.restoX.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.laraim237.restoX.entity.Variant;

public interface VariantRepository extends JpaRepository<Variant, String> {

	Optional<Variant> findByIdAndMenuItem_IdAndMenuItem_Category_Restaurant_Id(String variantId, String itemId,
			String restaurantId);

	List<Variant> findByMenuItem_IdAndMenuItem_Category_Restaurant_Id(String itemId, String restaurantId);

}
