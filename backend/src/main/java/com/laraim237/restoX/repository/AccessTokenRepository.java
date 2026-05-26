package com.laraim237.restoX.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.laraim237.restoX.entity.AccessToken;
import com.laraim237.restoX.entity.User;
import com.laraim237.restoX.enums.TokenType;

import jakarta.validation.constraints.NotBlank;

@Repository
public interface AccessTokenRepository extends JpaRepository<AccessToken, String> {

	Optional<AccessToken> findByUserAndType(User user, TokenType registration);

	Optional<AccessToken> findByTokenAndType(String token, TokenType invitation);
	
	@Modifying
	@Query("DELETE FROM AccessToken a WHERE a.user = :user AND a.type = :type")
	void deleteByUserAndType(@Param("user") User user, @Param("type") TokenType type);

}
