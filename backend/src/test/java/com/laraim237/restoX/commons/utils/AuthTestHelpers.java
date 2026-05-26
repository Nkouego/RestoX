package com.laraim237.restoX.commons.utils;

import org.springframework.beans.factory.annotation.Autowired;

import com.laraim237.restoX.entity.AccessToken;
import com.laraim237.restoX.entity.User;
import com.laraim237.restoX.enums.TokenType;

import tools.jackson.databind.ObjectMapper;



public class AuthTestHelpers {
	
	@Autowired static final ObjectMapper objectMapper = new ObjectMapper();
	
	public static final String AUTH_BASE_URL = "/api/v1/auth";
	
	public static User buildUser(boolean enabled) {
		return User.builder()
				.id("1")
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

	
	public static String json(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

}
