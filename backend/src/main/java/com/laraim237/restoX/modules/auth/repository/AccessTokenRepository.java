package com.laraim237.restoX.modules.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.laraim237.restoX.modules.auth.entity.AccessToken;
import com.laraim237.restoX.modules.auth.enums.TokenType;
import com.laraim237.restoX.modules.user.User;

@Repository
public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {

	Optional<AccessToken> findByUserAndType(User user, TokenType registration);

}
