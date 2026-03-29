package com.laraim237.restoX.modules.auth.service;

import com.laraim237.restoX.modules.user.User;

public interface JwtService {

	String generateToken(User user);

}
