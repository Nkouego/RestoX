package com.laraim237.restoX.commons.utils;

import com.laraim237.restoX.modules.auth.entity.AccessToken;
import com.laraim237.restoX.modules.auth.enums.TokenType;
import com.laraim237.restoX.modules.user.User;

public class AuthTestHelpers {
	
	public static User buildUser(boolean enabled) {
		return User.builder()
				.id(1L)
				.firstName("Jean")
				.lastName("Jacques")
				.enabled(enabled)
				.email("jeanjacques@exemple.com")
				.password("encodedPassword")
				.build();
	}
	
	public static AccessToken buildToken(User user, TokenType type, boolean expired) {
		int minutes = expired? -1 : 15;
		return new AccessToken("123456", user, minutes, type);
	}

}
