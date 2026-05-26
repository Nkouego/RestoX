package com.laraim237.restoX.common.utils;

import org.springframework.data.jpa.domain.Specification;

import com.laraim237.restoX.entity.RestaurantUser;
import com.laraim237.restoX.enums.StaffStatus;


public class StaffSpecification {
	
	public static Specification<RestaurantUser> byRestaurant(String restaurantId) {
		return (root, query, cb) ->
			cb.equal(root.get("restaurant").get("id"), restaurantId);
	}
	
	public static Specification<RestaurantUser> byStatus(StaffStatus status) {
		return (root, query, cb) ->
		status ==  null ? null : cb.equal(root.get("status"), status);
	}
	
	public static Specification<RestaurantUser> byName(String search) {
		return (root, query, cb) ->
		search == null ? null : cb.or(cb.like(cb.lower(root.get("user").get("firstName")), "%" + search.toLowerCase() +"%"),
		                        cb.like(cb.lower(root.get("user").get("lastName")), "%" + search.toLowerCase() +"%"));
	}

}
