package com.laraim237.restoX.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.laraim237.restoX.dto.MenuItemDto.AddMenuItemRequest;
import com.laraim237.restoX.dto.MenuItemDto.MenuItemResponse;
import com.laraim237.restoX.dto.MenuItemDto.UpdateMenuItemRequest;
import com.laraim237.restoX.entity.MenuItem;

@Mapper(componentModel = "Spring")
public interface MenuItemMapper {
	
	@BeanMapping(ignoreByDefault = true)
	@Mapping(target = "name")
	@Mapping(target = "description")
	@Mapping(target = "price")
	@Mapping(target = "available", constant = "true")
	MenuItem toEntity(AddMenuItemRequest request);
	
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    MenuItemResponse toResponse(MenuItem menuItem);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category.id", source = "categoryId")
    void updateMenuItem(UpdateMenuItemRequest request, @MappingTarget MenuItem menuItem);

}
