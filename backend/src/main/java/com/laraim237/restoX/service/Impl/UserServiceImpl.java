package com.laraim237.restoX.service.Impl;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.laraim237.restoX.common.Exception.ResourceNotFoundException;
import com.laraim237.restoX.common.utils.JwtUtils;
import com.laraim237.restoX.dto.StorageResult;
import com.laraim237.restoX.dto.UserDto.ChangePasswordRequest;
import com.laraim237.restoX.dto.UserDto.UpdateProfileRequest;
import com.laraim237.restoX.dto.UserDto.UserProfileResponse;
import com.laraim237.restoX.entity.RestaurantUser;
import com.laraim237.restoX.entity.User;
import com.laraim237.restoX.mapper.UserMapper;
import com.laraim237.restoX.repository.RestaurantUserRepository;
import com.laraim237.restoX.repository.UserRepository;
import com.laraim237.restoX.service.StorageService;
import com.laraim237.restoX.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	
	private final JwtUtils jwtUtils;
	
	private final UserMapper userMapper;
	
	private final UserRepository userRepository;
	private final RestaurantUserRepository restaurantUserRepository;
	
	private final StorageService storageService;
	private final PasswordService passwordService;
	private final RestaurantSecurityService restaurantSecurityService;

	@Override
	public UserProfileResponse getMyProfile(Authentication authentication) {
		//1.On recupere les infos de l'utilisateur
		User user = restaurantSecurityService.getAuthenticatedUser(authentication);
		
		//2.On recupere ses infos dans le restaurant actif
		String currentRestaurantId = jwtUtils.getCurrentRestaurantId(authentication);
		
		if (currentRestaurantId == null) {
			return userMapper.toProfileResponse(user); 
		}
		
		RestaurantUser restaurantUser = restaurantUserRepository
                .findByRestaurantIdAndUserId(currentRestaurantId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not in this restaurant"));
	
		return userMapper.toProfileResponse(user, restaurantUser);
	}

	@Override
	public UserProfileResponse updateProfile(UpdateProfileRequest request, Authentication authentication) throws IOException {
		//1.On recupere les infos de l'utilisateur
				User user = restaurantSecurityService.getAuthenticatedUser(authentication);
				
		//2.On modifie ses infos
		userMapper.UpdateProfile(request, user);
		
		if(request.picture() != null && !request.picture().isEmpty() ) {
			// Supprime l'ancienne photo si elle existe
	        if (user.getPicturePublicId() != null) {
	            storageService.delete(user.getPicturePublicId());
	        } 
	        
	        //enregistre la nouvelle image
	        String folder = String.format("restoX/users/picture");
	        StorageResult result = storageService.uploadFile(request.picture(), folder);
	        user.setPicturePublicId(result.publicId());
	        user.setPictureUrl(result.url());
		}
		
		userRepository.save(user);
		
		//3.retourne le profil mis a jour
		String currentRestaurantId = jwtUtils.getCurrentRestaurantId(authentication);
		
		if(currentRestaurantId == null) {
			return userMapper.toProfileResponse(user);
		}
		
		RestaurantUser restaurantUser = restaurantUserRepository.findByRestaurantIdAndUserId(currentRestaurantId, user.getId())
				.orElseThrow(() -> new ResourceNotFoundException("User not in this restaurant"));
		
		return userMapper.toProfileResponse(user, restaurantUser);
	}

	@Override
	public void requestChangePassword(Authentication authentication, HttpServletRequest httpRequest) {
		//1.On recupere les infos de l'utilisateur
		User user = restaurantSecurityService.getAuthenticatedUser(authentication);
		
		//2.On envoie un mail pour changement mot de passe
		passwordService.sendPasswordResetCode(user, httpRequest);
	}

	@Override
	public void confirmChangePassword(ChangePasswordRequest request, Authentication authentication,
			HttpServletRequest httpRequest) {
		//1.On recupere les infos de l'utilisateur
		User user = restaurantSecurityService.getAuthenticatedUser(authentication);
		
		//2.On envoie un mail pour changement mot de passe
		passwordService.confirmPasswordReset(user, request.code(), request.newPassword(), httpRequest);	
	}

}
