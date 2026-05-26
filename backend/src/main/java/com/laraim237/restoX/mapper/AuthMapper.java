package com.laraim237.restoX.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.laraim237.restoX.dto.AuthDto;
import com.laraim237.restoX.dto.AuthDto.AuthResponse;
import com.laraim237.restoX.entity.Restaurant;
import com.laraim237.restoX.entity.RestaurantUser;
import com.laraim237.restoX.entity.User;

@Mapper(componentModel = "spring")
public interface AuthMapper {
	
    @Mapping(target = "password", ignore = true)
	User toUser(AuthDto.RegisterRequest request);
	
 
    @Mapping(target = "token", ignore = true)
    @Mapping(target = "refreshToken", ignore = true)
    @Mapping(target = "message", ignore = true)
    AuthResponse toAuthResponse(User user);
}
