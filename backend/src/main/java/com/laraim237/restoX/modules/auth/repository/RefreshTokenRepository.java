package com.laraim237.restoX.modules.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.laraim237.restoX.modules.auth.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	
	@Modifying
	@Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.user.id = :userId")
	void revokeAllByUserId(@Param("userId") Long userId);

}
