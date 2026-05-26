package com.laraim237.restoX.controller;

import java.io.IOException;

import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laraim237.restoX.dto.VariantDto.AddVariantRequest;
import com.laraim237.restoX.dto.VariantDto.VariantResponse;
import com.laraim237.restoX.service.VariantService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/restaurants/{restaurantId}/menu/variants")

public class VariantController {
	
	private final VariantService variantService;

	public ResponseEntity<VariantResponse> createVariant(AddVariantRequest request,@PathVariable String restaurantId, Authentication authentication) throws IOException{
		return ResponseEntity.ok(variantService.createVariant(request, restaurantId, authentication));
	}
}
