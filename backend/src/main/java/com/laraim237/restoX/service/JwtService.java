package com.laraim237.restoX.service;

import com.laraim237.restoX.entity.User;

public interface JwtService {

	String generateToken(User user, String currentRestaurantId);

}
