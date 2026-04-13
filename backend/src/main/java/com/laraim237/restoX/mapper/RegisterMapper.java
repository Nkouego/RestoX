package com.laraim237.restoX.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.laraim237.restoX.dto.AuthDto;
import com.laraim237.restoX.entity.Restaurant;
import com.laraim237.restoX.entity.RestaurantUser;
import com.laraim237.restoX.entity.User;

@Mapper(componentModel = "spring")
public interface RegisterMapper {
	
    @Mapping(target = "password", ignore = true)
	User toUser(AuthDto.RegisterRequest request);
	
    
    @Mapping(target = "name", source = "restaurantName")
    @Mapping(target = "description", source = "restaurantDescription")
    @Mapping(target = "address", source = "restaurantAddress")
    @Mapping(target = "active", constant = "true")
    Restaurant toRestaurant(AuthDto.RegisterRequest request);
}
