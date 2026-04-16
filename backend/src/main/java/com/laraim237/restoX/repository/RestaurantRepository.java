package com.laraim237.restoX.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.laraim237.restoX.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
	
	@Query("""
			SELECT r FROM Restaurant r
			JOIN r.restaurantUsers ru
			WHERE ru.user.id = :userId
			AND r.active = true
		  """)
	List<Restaurant>  findAllByUserId(@Param("userId") Long userId);

	@Query("""
			SELECT r FROM Restaurant r
			JOIN r.restaurantUsers ru
			WHERE r.id = :restaurantId
			AND ru.user.id = :userId
			""")
	Optional<Restaurant> findByIdAndUserId(@Param("restaurantId") Long restaurantId, @Param("userId")  Long userId);

}
