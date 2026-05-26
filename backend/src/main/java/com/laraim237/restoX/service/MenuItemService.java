package com.laraim237.restoX.service;

import java.io.IOException;

import org.springframework.data.domain.Page;

import com.laraim237.restoX.dto.MenuItemDto.AddMenuItemRequest;
import com.laraim237.restoX.dto.MenuItemDto.MenuItemFilterRequest;
import com.laraim237.restoX.dto.MenuItemDto.MenuItemResponse;
import com.laraim237.restoX.dto.MenuItemDto.UpdateMenuItemRequest;

public interface MenuItemService {

	MenuItemResponse createItem(AddMenuItemRequest request, String restaurantId) throws IOException;

	MenuItemResponse updateItem(UpdateMenuItemRequest request, String restaurantId, String categoryId) throws IOException;

	void delete(String restaurantid, String itemId);

	MenuItemResponse getItemById(String restaurantId, String itemId);

	Page<MenuItemResponse> listItems(MenuItemFilterRequest fiter, String restaurantid);

	

}
