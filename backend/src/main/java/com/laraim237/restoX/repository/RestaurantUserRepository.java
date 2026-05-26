package com.laraim237.restoX.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.laraim237.restoX.entity.Restaurant;
import com.laraim237.restoX.entity.RestaurantUser;
import com.laraim237.restoX.entity.User;
import com.laraim237.restoX.enums.RestaurantRole;
import com.laraim237.restoX.enums.StaffStatus;

import java.util.List;
import java.util.Optional;


public interface RestaurantUserRepository extends JpaRepository<RestaurantUser, String>, JpaSpecificationExecutor<RestaurantUser> {

	boolean existsByUserAndRestaurantAndRole(User staff, Restaurant restaurant,RestaurantRole role);
	
	Optional<RestaurantUser> findByRestaurantIdAndUserId(String restaurantId, String userId);

	List<RestaurantUser> findByRestaurantId(String restaurantId);

	boolean existsByUserId(String staffId);

	boolean existsByRestaurantIdAndUserId(String restaurantId, String userId);

}
