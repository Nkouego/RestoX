package com.laraim237.restoX.service.Impl;

import java.io.IOException;
import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.laraim237.restoX.common.Exception.ResourceNotFoundException;
import com.laraim237.restoX.common.utils.MenuItemSpecification;
import com.laraim237.restoX.dto.MenuItemDto.AddMenuItemRequest;
import com.laraim237.restoX.dto.MenuItemDto.MenuItemFilterRequest;
import com.laraim237.restoX.dto.MenuItemDto.MenuItemResponse;
import com.laraim237.restoX.dto.MenuItemDto.UpdateMenuItemRequest;
import com.laraim237.restoX.dto.StorageResult;
import com.laraim237.restoX.entity.Category;
import com.laraim237.restoX.entity.MenuItem;
import com.laraim237.restoX.mapper.MenuItemMapper;
import com.laraim237.restoX.repository.CategoryRepository;
import com.laraim237.restoX.repository.MenuItemRepository;
import com.laraim237.restoX.service.MenuItemService;
import com.laraim237.restoX.service.StorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MenuItemServiceImp implements MenuItemService {
	
	private static final String folder = "restoX/restaurants/%s/items";
	private final CategoryRepository categoryRepository;
	private final MenuItemRepository menuItemRepository;
	private final MenuItemMapper menuItemMapper;
	private final StorageService storageService;

	@Override
	public MenuItemResponse createItem(AddMenuItemRequest request, String restaurantId) throws IOException {
        //1.On retourne la categorie
		Category category = categoryRepository.findByIdAndRestaurant_Id(request.categoryId(), restaurantId)
	            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
		
		//2.Mapper le DTO vers l'entité
        MenuItem menuItem = menuItemMapper.toEntity(request);
        
        //3.On upload l'image
        if(request.image() != null && !request.image().isEmpty()) {
        	String folderItems = String.format(folder, restaurantId);
    		StorageResult storageResult = storageService.uploadFile(request.image(), folderItems);
    	    menuItem.setImageUrl(storageResult.url());
    	    menuItem.setImagePublicId(storageResult.publicId());
        }
		
        // 4. Lier la catégorie
        menuItem.setCategory(category);

        // 5. Sauvegarder et Retourner la réponse
        return menuItemMapper.toResponse( menuItemRepository.save(menuItem));
	}
	
	@Override
	public MenuItemResponse updateItem(UpdateMenuItemRequest request, String restaurantId, String itemId) throws IOException {
	   
		// 1. Récupérer le menuItem
	    MenuItem menuItem = menuItemRepository.findByIdAndCategory_Restaurant_Id(itemId, restaurantId)
	            .orElseThrow(() -> new ResourceNotFoundException("MenuItem not found"));

	    // 2. Mapper les champs modifiés
	    menuItemMapper.updateMenuItem(request, menuItem);

	    // 3. Gérer l'image
	    if (request.image() != null && !request.image().isEmpty()) {
	        // Supprimer l'ancienne image si elle existe
	        if (menuItem.getImagePublicId() != null) {
	            storageService.delete(menuItem.getImagePublicId());
	        }
	        // Uploader la nouvelle
	            String folderItems = String.format(folder, restaurantId);
	            StorageResult storageResult = storageService.uploadFile(request.image(), folderItems);
	            menuItem.setImageUrl(storageResult.url());
	            menuItem.setImagePublicId(storageResult.publicId());
	    }
	    
	    //4.Gerer la categorie
	    if(request.categoryId() != null) {
	    	Category category = categoryRepository.findByIdAndRestaurant_Id(request.categoryId(), restaurantId)
	    			.orElseThrow(()->new ResourceNotFoundException("Category not found"));
	    	menuItem.setCategory(category);
	    }

	    // 4. Sauvegarder et retourner
	    return menuItemMapper.toResponse(menuItemRepository.save(menuItem));
	}

	@Override
	public void delete(String restaurantId, String itemId) {
	    
		//1.Recupere le produit
	    MenuItem menuItem = menuItemRepository.findByIdAndCategory_Restaurant_Id(itemId, restaurantId)
	            .orElseThrow(() -> new ResourceNotFoundException("MenuItem not found"));
	    
	    // 2. Supprimer l'image sur Cloudinary si elle existe
	    if (menuItem.getImagePublicId() != null) {
	        try {
	            storageService.delete(menuItem.getImagePublicId());
	        } catch (IOException e) {
	            log.warn("Failed to delete image from Cloudinary: {}", e.getMessage());
	        }
	    }

	    // 3. Supprimer le produit
	    menuItem.setDeletedAt(Instant.now());
	    menuItemRepository.save(menuItem);
	}

	@Override
	public MenuItemResponse getItemById(String restaurantId, String itemId) {
	   
		//1.On recupere le menuItem
	    MenuItem menuItem = menuItemRepository.findByIdAndCategory_Restaurant_Id(itemId, restaurantId)
	            .orElseThrow(() -> new ResourceNotFoundException("MenuItem not found"));
	    
		return menuItemMapper.toResponse(menuItem);
	}

	@Override
	public Page<MenuItemResponse> listItems(MenuItemFilterRequest filter, String restaurantId) {

	    //1.Recuperer la liste des produits avec filtres si presents
	    Specification<MenuItem> spec = Specification
	    		.where(MenuItemSpecification.byRestaurant(restaurantId))
	    		.and(MenuItemSpecification.byCategory(filter.categoryId()))
	    		.and(MenuItemSpecification.byAvailable(filter.available()))
	    		.and(MenuItemSpecification.byName(filter.search()));
	    
	    //2. On cree une pagination
	    Pageable pageable = PageRequest.of(filter.page(), filter.size());
		
		return menuItemRepository.findAll(spec, pageable).map(menuItemMapper::toResponse);
	}

}
