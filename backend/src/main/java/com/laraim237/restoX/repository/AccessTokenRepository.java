package com.laraim237.restoX.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.laraim237.restoX.entity.AccessToken;
import com.laraim237.restoX.entity.User;
import com.laraim237.restoX.enums.TokenType;

@Repository
public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {

	Optional<AccessToken> findByUserAndType(User user, TokenType registration);

}
