package com.laraim237.restoX.service.Impl;

import java.util.List;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.laraim237.restoX.dto.RestaurantDto;
import com.laraim237.restoX.dto.RestaurantDto.CreateRestaurantRequest;
import com.laraim237.restoX.dto.RestaurantDto.RestaurantResponse;
import com.laraim237.restoX.dto.RestaurantDto.UpdateRestaurantRequest;
import com.laraim237.restoX.entity.Restaurant;
import com.laraim237.restoX.entity.RestaurantUser;
import com.laraim237.restoX.entity.User;
import com.laraim237.restoX.enums.AuditAction;
import com.laraim237.restoX.enums.RestaurantRole;
import com.laraim237.restoX.enums.StaffStatus;
import com.laraim237.restoX.mapper.RestaurantMapper;
import com.laraim237.restoX.repository.RestaurantRepository;
import com.laraim237.restoX.repository.RestaurantUserRepository;
import com.laraim237.restoX.repository.UserRepository;
import com.laraim237.restoX.service.AuditService;
import com.laraim237.restoX.service.RestaurantService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class RestaurantServiceImpl implements RestaurantService {
	private final RestaurantMapper restaurantMapper;
	private final RestaurantRepository restaurantRepository;
	private final RestaurantUserRepository restaurantUserRepository;
	private final UserRepository userRepository;
	private final AuditService auditService;

	@Override
	public RestaurantResponse create(CreateRestaurantRequest request, HttpServletRequest httpRequest, Authentication authentication) {
		// 1. On recupere l'id de l'utilisateur du jwt
		Jwt jwt = (Jwt)authentication.getPrincipal();
		Long userId = jwt.getClaim("userId");
		
		//2.On recupere l'utilisateur en base 
		User user = userRepository.findById(userId)
		            .orElseThrow(()-> new EntityNotFoundException("User not found"));
		
		//3.On cree le restaurant
		Restaurant restaurant = restaurantMapper.toRestaurant(request);
		restaurantRepository.save(restaurant);
		
		//4.On lie le user au restaurant avec le role admin
		RestaurantUser restaurantUser = RestaurantUser.builder()
				.user(user)
				.restaurant(restaurant)
				.role(RestaurantRole.ADMIN)
				.status(StaffStatus.ACTIVE)
				.build();
		restaurantUserRepository.save(restaurantUser);
		
		//5.audit
		auditService.log(AuditAction.RESTAURANT_CREATED, restaurant.getId(),
	            "restaurant", null, null, restaurant.getId(), userId, httpRequest);

	    return restaurantMapper.toResponse(restaurant);
	}

	@Override
	@Transactional(readOnly = true)
	public List<RestaurantResponse> getMyRestaurants(Authentication authentication, HttpServletRequest httpRequest) {
		// 1. On recupere l'id de l'utilisateur du jwt
		Jwt jwt = (Jwt)authentication.getPrincipal();
		Long userId = jwt.getClaim("userId");
				
	    //2. Retourne les restaurants
		return restaurantRepository.findAllByUserId(userId)
				.stream()
				.map(restaurantMapper::toResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public RestaurantResponse getById(Long id, Authentication authentication, HttpServletRequest httpRequest) {
		// 1. On recupere l'id de l'utilisateur du jwt
		Jwt jwt = (Jwt)authentication.getPrincipal();
		Long userId = jwt.getClaim("userId");
		
		//2.on recupere le restaurant en question
		Restaurant restaurant = restaurantRepository.findByIdAndUserId(id, userId)
				               .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));
 
		return restaurantMapper.toResponse(restaurant);
	}

	@Override
	public RestaurantResponse update(UpdateRestaurantRequest request, 
			                         Long id, 
			                         Authentication authentication, 
			                         HttpServletRequest httpRequest) {
		// 1. On recupere l'id de l'utilisateur du jwt
		Jwt jwt = (Jwt)authentication.getPrincipal();
		Long userId = jwt.getClaim("userId");
		
		//2.on recupere le restaurant en question
		Restaurant restaurant = restaurantRepository.findByIdAndUserId(id, userId)
				               .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

		
		//3. On applique les changements sur le restaurant en question
		restaurantMapper.updateRestaurant(request, restaurant);
		restaurantRepository.save(restaurant);
		
		auditService.log(AuditAction.RESTAURANT_UPDATED, restaurant.getId(),
	            "restaurant", null, null, restaurant.getId(), userId, httpRequest);

	    return restaurantMapper.toResponse(restaurant);
	} 
	
	@Override
    public RestaurantResponse archive(Long id, Authentication authentication, HttpServletRequest httpRequest) {
		// 1. On recupere l'id de l'utilisateur du jwt
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");

        //2.on recupere le restaurant en question
        Restaurant restaurant = restaurantRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        //3.On desactive le restaurant
        restaurant.setActive(false);
        restaurantRepository.save(restaurant);

        //4.audit
        auditService.log(AuditAction.RESTAURANT_ARCHIVED, restaurant.getId(),
            "restaurant", null, null, restaurant.getId(), userId, null);
        
        return RestaurantResponse.builder()
        		.message("restaurant archive successfully")
        		.build();
    }

}
