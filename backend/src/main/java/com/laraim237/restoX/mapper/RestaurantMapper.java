package com.laraim237.restoX.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.laraim237.restoX.dto.RestaurantDto;
import com.laraim237.restoX.dto.RestaurantDto.CreateRestaurantRequest;
import com.laraim237.restoX.dto.RestaurantDto.RestaurantResponse;
import com.laraim237.restoX.dto.RestaurantDto.UpdateRestaurantRequest;
import com.laraim237.restoX.entity.Restaurant;

@Mapper(componentModel = "spring")
public interface RestaurantMapper {
	
	 @Mapping(target = "active", constant = "true")
	 Restaurant toRestaurant(RestaurantDto.CreateRestaurantRequest request);

	 RestaurantResponse toResponse(Restaurant restaurant);

	 @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE )
	 void updateRestaurant(UpdateRestaurantRequest request, @MappingTarget Restaurant restaurant);
}
