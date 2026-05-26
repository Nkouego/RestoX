package com.laraim237.restoX.service;

import com.laraim237.restoX.entity.AccessToken;
import com.laraim237.restoX.entity.User;
import com.laraim237.restoX.enums.TokenType;

public interface AccessTokenService {
	AccessToken generate(User user, TokenType type, int expirationMinutes);
    AccessToken validate(User user, TokenType type, String code);
}
