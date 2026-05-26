package com.laraim237.restoX.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.laraim237.restoX.dto.UserDto.UpdateProfileRequest;
import com.laraim237.restoX.dto.UserDto.UserProfileResponse;
import com.laraim237.restoX.entity.RestaurantUser;
import com.laraim237.restoX.entity.User;

@Mapper(componentModel = "Spring")
public interface UserMapper {
	
	@Mapping(source="user.id", target = "id")
	@Mapping(source="restaurantUser.restaurant.status", target="restaurantStatus")
	@Mapping(source="restaurantUser.status", target="staffStatus")
	@Mapping(source="restaurantUser.role", target="role")
	@Mapping(source="restaurantUser.assignedAt", target="assignedAt")
	@Mapping(source="user.systemRole", target="systemRole", ignore=true )
	UserProfileResponse toProfileResponse(User user, RestaurantUser restaurantUser);
	
	@Mapping(target = "restaurantStatus", ignore = true)
    @Mapping(target = "staffStatus", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "assignedAt", ignore = true)
	@Mapping(target = "systemRole", source="user.systemRole")
	UserProfileResponse toProfileResponse(User user);
	
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, ignoreByDefault = true)
	void UpdateProfile(UpdateProfileRequest request, @MappingTarget User user);
	
	
}
