package com.laraim237.restoX.service.Impl;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.laraim237.restoX.common.Exception.ResourceNotFoundException;
import com.laraim237.restoX.dto.StorageResult;
import com.laraim237.restoX.dto.VariantDto.AddVariantRequest;
import com.laraim237.restoX.dto.VariantDto.UpdateVariantRequest;
import com.laraim237.restoX.dto.VariantDto.VariantResponse;
import com.laraim237.restoX.entity.MenuItem;
import com.laraim237.restoX.entity.Variant;
import com.laraim237.restoX.mapper.VariantMapper;
import com.laraim237.restoX.repository.MenuItemRepository;
import com.laraim237.restoX.repository.VariantRepository;
import com.laraim237.restoX.service.StorageService;
import com.laraim237.restoX.service.VariantService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class VariantServiceImpl implements VariantService {
	
    private static final String folder = "restoX/restaurants/%s/variants";

	private final StorageService storageService;
	private final VariantMapper variantMapper;
	private final MenuItemRepository menuItemRepository;
	private final VariantRepository variantRepository;
	
	@Override
	public VariantResponse createVariant(AddVariantRequest request, String restaurantId, Authentication authentication) throws IOException {
		 // 1. Vérifier que le MenuItem appartient au restaurant
        MenuItem menuItem = menuItemRepository.findByIdAndCategory_Restaurant_Id(request.menuItemId(), restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem not found"));

        // 2. Mapper
        Variant variant = variantMapper.toEntity(request);
        variant.setMenuItem(menuItem);

        // 3. Upload image
        String folderVariants = String.format(folder, restaurantId);
        StorageResult result = storageService.uploadFile(request.image(), folderVariants);
        variant.setImageUrl(result.url());
        variant.setImagePublicId(result.publicId());

        return variantMapper.toResponse(variantRepository.save(variant));
	}
	
	@Override
    public VariantResponse updateVariant(UpdateVariantRequest request, String restaurantId, String itemId, String variantId) throws IOException {
        // 1. Récupérer le variant
        Variant variant = variantRepository.findByIdAndMenuItem_IdAndMenuItem_Category_Restaurant_Id(variantId, itemId, restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found"));

        // 2. Mapper les champs modifiés
        variantMapper.updateVariant(request, variant);

        // 3. Gérer l'image
        if (request.image() != null && !request.image().isEmpty()) {
            if (variant.getImagePublicId() != null) {
                storageService.delete(variant.getImagePublicId());
            }
            String folderVariants = String.format(folder, restaurantId);
            StorageResult result = storageService.uploadFile(request.image(), folderVariants);
            variant.setImageUrl(result.url());
            variant.setImagePublicId(result.publicId());
        }

        return variantMapper.toResponse(variantRepository.save(variant));
    }

   

    @Override
    public void deleteVariant(String restaurantId, String itemId, String variantId) throws IOException {
        Variant variant = variantRepository.findByIdAndMenuItem_IdAndMenuItem_Category_Restaurant_Id(variantId, itemId, restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found"));

        if (variant.getImagePublicId() != null) {
                storageService.delete(variant.getImagePublicId());
        }

        variant.setDeletedAt(Instant.now());
        variantRepository.save(variant);
    }

    @Transactional(readOnly = true)
    @Override
    public List<VariantResponse> listVariants(String restaurantId, String itemId) {
        return variantRepository.findByMenuItem_IdAndMenuItem_Category_Restaurant_Id(itemId, restaurantId)
                .stream()
                .map(variantMapper::toResponse)
                .toList();
    }

}
