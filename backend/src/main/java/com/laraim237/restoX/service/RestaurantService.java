package com.laraim237.restoX.service;

import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;

import com.laraim237.restoX.dto.RestaurantDto.CreateRestaurantRequest;
import com.laraim237.restoX.dto.RestaurantDto.RestaurantResponse;
import com.laraim237.restoX.dto.RestaurantDto.UpdateRestaurantRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

public interface RestaurantService {

	RestaurantResponse create(@Valid CreateRestaurantRequest request, HttpServletRequest httpRequest, Authentication authentication);

	List<RestaurantResponse> getMyRestaurants(Authentication authentication, HttpServletRequest httpRequest);

	RestaurantResponse getById(Long id, Authentication authentication, HttpServletRequest httpRequest);

	RestaurantResponse update(UpdateRestaurantRequest request, Long id, Authentication authentication, HttpServletRequest httpRequest);

	RestaurantResponse archive(Long id, Authentication authentication, HttpServletRequest httpRequest);

}
