package com.laraim237.restoX.service.Impl;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.laraim237.restoX.common.Exception.ResourceNotFoundException;
import com.laraim237.restoX.common.utils.JwtUtils;
import com.laraim237.restoX.dto.RestaurantDto.CreateRestaurantRequest;
import com.laraim237.restoX.dto.RestaurantDto.RestaurantResponse;
import com.laraim237.restoX.dto.RestaurantDto.SwitchRestaurantRequest;
import com.laraim237.restoX.dto.RestaurantDto.UpdateRestaurantRequest;
import com.laraim237.restoX.entity.Restaurant;
import com.laraim237.restoX.entity.RestaurantUser;
import com.laraim237.restoX.entity.User;
import com.laraim237.restoX.enums.AuditAction;
import com.laraim237.restoX.enums.RestaurantRole;
import com.laraim237.restoX.enums.RestaurantStatus;
import com.laraim237.restoX.enums.StaffStatus;
import com.laraim237.restoX.mapper.RestaurantMapper;
import com.laraim237.restoX.repository.RestaurantRepository;
import com.laraim237.restoX.repository.RestaurantUserRepository;
import com.laraim237.restoX.service.AuditService;
import com.laraim237.restoX.service.JwtService;
import com.laraim237.restoX.service.RestaurantService;

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
	private final AuditService auditService;
	private final JwtService jwtService;
	private final RestaurantSecurityService restaurantSecurityService;
	private final JwtUtils jwtUtils;
	
	@Override
	public RestaurantResponse create(CreateRestaurantRequest request, HttpServletRequest httpRequest, Authentication authentication) {		
		//1.On recupere l'admin en base 
		User admin = restaurantSecurityService.getAuthenticatedUser(authentication);
		
		//2.On cree le restaurant
		Restaurant restaurant = restaurantMapper.toRestaurant(request);
		restaurantRepository.save(restaurant);
		
		//3.On lie le user au restaurant avec le role admin
		RestaurantUser restaurantUser = RestaurantUser.builder()
				.user(admin)
				.restaurant(restaurant)
				.role(RestaurantRole.ADMIN)
				.status(StaffStatus.ACTIVE)
				.build();
		restaurantUserRepository.save(restaurantUser);
		
		//4.audit
		auditService.log(AuditAction.RESTAURANT_CREATED, restaurant.getId(),
	            "restaurant", null, null, restaurant.getId(), admin.getId(), httpRequest);

	    return restaurantMapper.toResponse(restaurant, restaurantUser);
	}

	@Override
	@Transactional(readOnly = true)
	public List<RestaurantResponse> getMyRestaurants(Authentication authentication, HttpServletRequest httpRequest) {
		 User user = restaurantSecurityService.getAuthenticatedUser(authentication);

	        return user.getRestaurantUsers().stream()
	                .filter(ru -> ru.getRestaurant().getStatus() != RestaurantStatus.ARCHIVED)
	                .map(ru -> restaurantMapper.toResponse(ru.getRestaurant(), ru))
	                .toList();
	}

	@Override
	@Transactional(readOnly = true)
	public RestaurantResponse getById(String restaurantId, Authentication authentication, HttpServletRequest httpRequest) {
		// 1. extrait l'id de l'admin
		String userId = jwtUtils.getUserId(authentication);
		
		//2.recupre le restaurant
		Restaurant restaurant = restaurantRepository.findById(restaurantId)
			        .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));	
		
		RestaurantUser restaurantUser = restaurantUserRepository
	                .findByRestaurantIdAndUserId(restaurantId, userId)
	                .orElseThrow(() -> new ResourceNotFoundException("User not in this restaurant"));
		
		return restaurantMapper.toResponse(restaurant, restaurantUser);
	}

	@Override
	public RestaurantResponse update(UpdateRestaurantRequest request, 
			                         String id, 
			                         Authentication authentication, 
			                         HttpServletRequest httpRequest) {
		// 1. extrait l'id de l'admin
		String adminId = jwtUtils.getUserId(authentication);
		
		Restaurant restaurant = restaurantRepository.findById(id)
		        .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
		
		RestaurantUser restaurantUser = restaurantUserRepository
                .findByRestaurantIdAndUserId(id, adminId)
                .orElseThrow(() -> new ResourceNotFoundException("User not in this restaurant"));

		//3. On applique les changements sur le restaurant en question
		restaurantMapper.updateRestaurant(request, restaurant);
		restaurantRepository.save(restaurant);
		
		auditService.log(AuditAction.RESTAURANT_UPDATED, restaurant.getId(),
	            "restaurant", null, null, restaurant.getId(), adminId, httpRequest);

	    return restaurantMapper.toResponse(restaurant, restaurantUser);
	} 
	
	
	
	@Override
    public RestaurantResponse archive(String restaurantId, Authentication authentication, HttpServletRequest httpRequest) {
		// 1. extrait l'id de l'admin
		String adminId = jwtUtils.getUserId(authentication);
		
		//2.On recupere le restaurant
		Restaurant restaurant = restaurantRepository.findById(restaurantId)
		        .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
		
        //3.On desactive le restaurant
        restaurant.setStatus(RestaurantStatus.ARCHIVED);;
        restaurantRepository.save(restaurant);

        //4.audit
        auditService.log(AuditAction.RESTAURANT_ARCHIVED, restaurant.getId(),
            "restaurant", null, null, restaurant.getId(), adminId, null);
        
        return RestaurantResponse.builder()
        		.message("restaurant archive successfully")
        		.build();
    }
	@Override
	public RestaurantResponse deactivate(String restaurantId, Authentication authentication, HttpServletRequest httpRequest) {
		// 1. extrait l'id de l'admin
		String adminId = jwtUtils.getUserId(authentication);
		
		//2.On recupere le restaurant
		Restaurant restaurant = restaurantRepository.findById(restaurantId)
		        .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
		
		if(restaurant.getStatus() != RestaurantStatus.ACTIVE) {
			throw new IllegalStateException("Impossible to deactivate this restaurant");
		}
		
		//3.On desactive le restaurant
		restaurant.setStatus(RestaurantStatus.INACTIVE);;
		restaurantRepository.save(restaurant);
		
		//4.audit
		auditService.log(AuditAction.RESTAURANT_DEACTIVATED, restaurant.getId(),
				"restaurant", null, null, restaurant.getId(), adminId, null);
		
		return RestaurantResponse.builder()
				.message("restaurant inactivate successfully")
				.build();
	}
	@Override
	public RestaurantResponse activate(String restaurantId, Authentication authentication, HttpServletRequest httpRequest) {
		// 1. extrait l'id de l'admin
		String adminId = jwtUtils.getUserId(authentication);
		
		//2.On recupere le restaurant
		Restaurant restaurant = restaurantRepository.findById(restaurantId)
		        .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
		
		if(restaurant.getStatus() != RestaurantStatus.INACTIVE) {
			throw new IllegalStateException("Impossible to activate this restaurant");
		}
		
		//3.On desactive le restaurant
		restaurant.setStatus(RestaurantStatus.ACTIVE);
		restaurantRepository.save(restaurant);
		
		//4.audit
		auditService.log(AuditAction.RESTAURANT_ACTIVATED, restaurant.getId(),
				"restaurant", null, null, restaurant.getId(), adminId, null);
		
		return RestaurantResponse.builder()
				.message("restaurant activate successfully")
				.build();
	}

	@Override
	public RestaurantResponse switchRestaurant(@Valid SwitchRestaurantRequest request, Authentication authentication) {
		//1.On verifie sil est menbre du restaurant
		restaurantSecurityService.verifyMembership(request.idRestaurant(), authentication);
		
		//2.On recupere l'utilisateur auteur de la demande
		User user = restaurantSecurityService.getAuthenticatedUser(authentication);
		
		//3.On genere un nouveau token
		String token = jwtService.generateToken(user, request.idRestaurant());
		
		return RestaurantResponse.builder()
				.token(token)
				.build();
	}

}
