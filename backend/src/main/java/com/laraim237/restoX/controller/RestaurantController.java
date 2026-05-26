package com.laraim237.restoX.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laraim237.restoX.dto.RestaurantDto.CreateRestaurantRequest;
import com.laraim237.restoX.dto.RestaurantDto.RestaurantResponse;
import com.laraim237.restoX.dto.RestaurantDto.SwitchRestaurantRequest;
import com.laraim237.restoX.dto.RestaurantDto.UpdateRestaurantRequest;
import com.laraim237.restoX.service.RestaurantService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/restaurants")
public class RestaurantController {
	
	private final RestaurantService restaurantService;
	
	@PostMapping
	public ResponseEntity<RestaurantResponse> create(@Valid @RequestBody CreateRestaurantRequest request, 
			                                         HttpServletRequest httpRequest, 
			                                         Authentication authentication){
		return ResponseEntity.status(HttpStatus.CREATED)
				             .body(restaurantService.create(request, httpRequest, authentication));	
	}
	
	@GetMapping
	public ResponseEntity<List<RestaurantResponse>> getMyRestaurants(Authentication authentication, 
			                                                         HttpServletRequest httpRequest){
		return ResponseEntity.ok(restaurantService.getMyRestaurants(authentication, httpRequest));
	}

	@GetMapping("/{id}")
	public ResponseEntity<RestaurantResponse> getById(@PathVariable String id, 
			                                          Authentication authentication, 
			                                          HttpServletRequest httpRequest){
		return ResponseEntity.ok(restaurantService.getById(id, authentication, httpRequest));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<RestaurantResponse>  update(@Valid @RequestBody UpdateRestaurantRequest request,
			                                          @PathVariable String id,
			                                          Authentication authentication,
			                                          HttpServletRequest httpRequest) {
		return ResponseEntity.ok(restaurantService.update(request, id, authentication, httpRequest));
	}
	
	@PostMapping("/switch")
	public ResponseEntity<RestaurantResponse> switchRestaurant(@Valid @RequestBody SwitchRestaurantRequest request,
	        											   Authentication authentication) {
	    return ResponseEntity.ok(restaurantService.switchRestaurant(request, authentication));
	}
	
	@PatchMapping("/{id}/archive")
	public ResponseEntity<RestaurantResponse> archive(@PathVariable String id, Authentication authentication, HttpServletRequest httpRequest) {	
		return ResponseEntity.ok(restaurantService.archive(id, authentication, httpRequest));
	}
	
	@PatchMapping("/{id}/activate")
	public ResponseEntity<RestaurantResponse> activate(@PathVariable String id, Authentication authentication, HttpServletRequest httpRequest) {	
		return ResponseEntity.ok(restaurantService.activate(id, authentication, httpRequest));
	}
	
	@PatchMapping("/{id}/desactivate")
	public ResponseEntity<RestaurantResponse> desactivate(@PathVariable String id, Authentication authentication, HttpServletRequest httpRequest) {	
		return ResponseEntity.ok(restaurantService.deactivate(id, authentication, httpRequest));
	}
}
