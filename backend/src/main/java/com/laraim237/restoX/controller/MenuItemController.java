package com.laraim237.restoX.controller;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laraim237.restoX.dto.MenuItemDto.AddMenuItemRequest;
import com.laraim237.restoX.dto.MenuItemDto.MenuItemFilterRequest;
import com.laraim237.restoX.dto.MenuItemDto.MenuItemResponse;
import com.laraim237.restoX.dto.MenuItemDto.UpdateMenuItemRequest;
import com.laraim237.restoX.service.MenuItemService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/restaurants/{restaurantId}/menu/items")
public class MenuItemController {
	
	private final MenuItemService menuItemService;
	
	@PostMapping
	public ResponseEntity<MenuItemResponse> createItem(@Valid @ModelAttribute AddMenuItemRequest request, 
												@PathVariable String restaurantId, 
											    Authentication authentication) throws IOException{
		return ResponseEntity.status(HttpStatus.CREATED).body(menuItemService.createItem(request, restaurantId));
	}
	
	@PutMapping("/{itemId}")
	public ResponseEntity<MenuItemResponse> updateItem(@Valid @ModelAttribute UpdateMenuItemRequest request,
	                                            @PathVariable String restaurantId,
	                                            @PathVariable String itemId,
	                                            Authentication authentication) throws IOException {
	    return ResponseEntity.ok(menuItemService.updateItem(request, restaurantId, itemId));
	}
	
	@DeleteMapping("/{itemId}")
	public ResponseEntity<Void> deleteItem(@PathVariable String restaurantId, @PathVariable String itemId, Authentication authentication){
		menuItemService.delete(restaurantId, itemId);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/{itemId}")
	public ResponseEntity<MenuItemResponse> getItemById(@PathVariable String restaurantId, @PathVariable String itemId, Authentication authentication){
		return ResponseEntity.ok(menuItemService.getItemById(restaurantId, itemId));
	}
	
	@GetMapping
	public ResponseEntity<Page<MenuItemResponse>> listItems(@ModelAttribute MenuItemFilterRequest filter,
												            @PathVariable String restaurantId,
												            Authentication authentication){
		return ResponseEntity.ok(menuItemService.listItems(filter, restaurantId));
	}

}
