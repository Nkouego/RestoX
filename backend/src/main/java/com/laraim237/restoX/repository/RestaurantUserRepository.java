package com.laraim237.restoX.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.laraim237.restoX.entity.RestaurantUser;

public interface RestaurantUserRepository extends JpaRepository<RestaurantUser, Long> {

}
