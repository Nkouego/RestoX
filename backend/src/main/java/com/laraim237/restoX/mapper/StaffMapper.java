package com.laraim237.restoX.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.laraim237.restoX.dto.StaffDto.AddStaffRequest;
import com.laraim237.restoX.dto.StaffDto.StaffResponse;
import com.laraim237.restoX.dto.StaffDto.UpdateStaffRequest;
import com.laraim237.restoX.entity.RestaurantUser;
import com.laraim237.restoX.entity.User;

@Mapper(componentModel = "spring")
public interface StaffMapper{
	
	@Mapping(target = "enabled", constant = "false")
	User toUser(AddStaffRequest request);
	
	@Mapping(target = "id", source = "staff.id")
	@Mapping(target = "message", ignore = true)
	StaffResponse toStaffResponse(User staff, RestaurantUser restaurantUser);
	
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateUser(UpdateStaffRequest request, @MappingTarget User user);
	
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateRestaurantUser(UpdateStaffRequest request, @MappingTarget RestaurantUser restaurantUser);

}
