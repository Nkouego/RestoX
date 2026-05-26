package com.laraim237.restoX.service.Impl;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.laraim237.restoX.common.Exception.RestaurantAccessDeniedException;
import com.laraim237.restoX.common.Exception.ResourceNotFoundException;
import com.laraim237.restoX.common.utils.JwtUtils;
import com.laraim237.restoX.entity.User;
import com.laraim237.restoX.repository.RestaurantUserRepository;
import com.laraim237.restoX.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RestaurantSecurityService {

    private final UserRepository userRepository;
    private final RestaurantUserRepository restaurantUserRepository;
    private final JwtUtils jwtUtils;

    //Verifie l'appartenance a un restaurant
    public void verifyMembership(String restaurantId, Authentication authentication) {
    	  String userId = jwtUtils.getUserId(authentication);

          if (!restaurantUserRepository.existsByRestaurantIdAndUserId(restaurantId, userId)) {
              throw new RestaurantAccessDeniedException("Access denied");
          }
    }

    //Retourne l'utilisateur authentifié
    public User getAuthenticatedUser(Authentication authentication) {
        return userRepository.findById(jwtUtils.getUserId(authentication))
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}