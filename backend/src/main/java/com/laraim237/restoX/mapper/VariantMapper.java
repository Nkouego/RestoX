package com.laraim237.restoX.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.laraim237.restoX.dto.VariantDto.AddVariantRequest;
import com.laraim237.restoX.dto.VariantDto.UpdateVariantRequest;
import com.laraim237.restoX.dto.VariantDto.VariantResponse;
import com.laraim237.restoX.entity.Variant;

@Mapper(componentModel = "spring")
public interface VariantMapper {

    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "imagePublicId", ignore = true)
	Variant toEntity(AddVariantRequest request);

	VariantResponse toResponse(Variant variant);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateVariant(UpdateVariantRequest request, @MappingTarget Variant variant);

}
