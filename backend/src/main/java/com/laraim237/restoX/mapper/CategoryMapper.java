package com.laraim237.restoX.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.laraim237.restoX.dto.CategoryDto.AddCategoryRequest;
import com.laraim237.restoX.dto.CategoryDto.CategoryResponse;
import com.laraim237.restoX.dto.CategoryDto.UpdateCategoryRequest;
import com.laraim237.restoX.entity.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

	CategoryResponse toResponse(Category category);
	
	@Mapping(target = "active", constant = "true")
	Category toEntity(AddCategoryRequest req);
	
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateCategory(UpdateCategoryRequest request, @MappingTarget Category category);
}
