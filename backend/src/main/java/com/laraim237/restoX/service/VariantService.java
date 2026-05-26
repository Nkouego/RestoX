package com.laraim237.restoX.service;

import java.io.IOException;
import java.util.List;

import org.springframework.security.core.Authentication;

import com.laraim237.restoX.dto.VariantDto.AddVariantRequest;
import com.laraim237.restoX.dto.VariantDto.UpdateVariantRequest;
import com.laraim237.restoX.dto.VariantDto.VariantResponse;

public interface VariantService {

	VariantResponse createVariant(AddVariantRequest request, String restaurantId, Authentication authentication) throws IOException;

	VariantResponse updateVariant(UpdateVariantRequest request, String restaurantId, String itemId, String variantId)
			throws IOException;

	void deleteVariant(String restaurantId, String itemId, String variantId) throws IOException;

	List<VariantResponse> listVariants(String restaurantId, String itemId);

}
