package com.laraim237.restoX.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.laraim237.restoX.dto.RestaurantDto.CreateRestaurantRequest;
import com.laraim237.restoX.dto.RestaurantDto.RestaurantResponse;
import com.laraim237.restoX.dto.RestaurantDto.UpdateRestaurantRequest;
import com.laraim237.restoX.entity.Restaurant;
import com.laraim237.restoX.entity.RestaurantUser;

@Mapper(componentModel = "spring")
public interface RestaurantMapper {
	
	
	 Restaurant toRestaurant(CreateRestaurantRequest request);

	 @Mapping(source = "restaurant.id", target = "id")
	 @Mapping(source = "restaurantUser.role", target = "role")
	 @Mapping(source = "restaurantUser.status", target = "staffStatus")
	 RestaurantResponse toResponse(Restaurant restaurant, RestaurantUser restaurantUser);

	 @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE )
	 void updateRestaurant(UpdateRestaurantRequest request, @MappingTarget Restaurant restaurant);
}
