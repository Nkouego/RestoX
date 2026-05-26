package com.laraim237.restoX.common.utils;

import org.springframework.data.jpa.domain.Specification;

import com.laraim237.restoX.entity.MenuItem;

public class MenuItemSpecification {
	
	public static Specification<MenuItem> byRestaurant(String restaurantId){
		return (root, query, cb) -> 
			cb.equal(root.get("category").get("restaurant").get("id"), restaurantId);
	}
	
	public static Specification<MenuItem> byCategory(String category){
		return (root, query, cb) ->
		category == null ? null : cb.equal(root.get("category").get("id"), category);
	}
	
	public static Specification<MenuItem> byAvailable(Boolean available){
		return (root, query, cb) ->
			available == null ? null : cb.equal(root.get("available"), available);
	}
	
	public static Specification<MenuItem> byName(String search){
		return (root, query, cb) ->
		search == null ? null : cb.like(cb.lower(root.get("name")), "%" +search.toLowerCase()+ "%");
	}

}
