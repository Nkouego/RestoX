package com.laraim237.restoX.modules.auth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.laraim237.restoX.modules.auth.dto.AuthDto;
import com.laraim237.restoX.modules.restaurant.Restaurant;
import com.laraim237.restoX.modules.restaurant.RestaurantUser;
import com.laraim237.restoX.modules.user.User;

@Mapper(componentModel = "spring")
public interface RegisterMapper {
	
    @Mapping(target = "password", ignore = true)
	User toUser(AuthDto.RegisterRequest request);
	
    
    @Mapping(target = "name", source = "restaurantName")
    @Mapping(target = "active", constant = "true")
    Restaurant toRestaurant(AuthDto.RegisterRequest request);
}
