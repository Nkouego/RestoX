package com.laraim237.restoX.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.laraim237.restoX.entity.Category;
import com.laraim237.restoX.entity.MenuItem;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, String>, JpaSpecificationExecutor<MenuItem> {

	Optional<MenuItem> findByIdAndCategory_Restaurant_Id(String itemId, String restaurantId);

}
