package com.laraim237.restoX.service.Impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.laraim237.restoX.entity.User;
import com.laraim237.restoX.service.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

	private final JwtEncoder jwtEncoder;

    @Value("${spring.security.jwt.expiration}")
    private long expiration;

	@Override
	public String generateToken(User user, String currentRestaurantId) {
		Instant now = Instant.now();
		
		  // Collecte tous les rôles (system + restaurant)
	    List<String> roles = new ArrayList<>();
	    if(user.getSystemRole() != null) {
	    	roles.add("ROLE_"+user.getSystemRole().name());
	    }
	    
	    user.getRestaurantUsers().forEach(ru->{
	    	roles.add("ROLE_"+ru.getRole().name());
	    });
	    
		//Definit les claims du jwt
	    JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
	    		.issuer("restoX")
	    		.subject(user.getEmail())
	    		.claim("userId", user.getId())
	    		.claim("roles", roles)
	    		.issuedAt(now)
	    		.expiresAt(now.plusSeconds(expiration));
	    
	   if(currentRestaurantId != null) {
		   claims.claim("currentRestaurantId", currentRestaurantId);
	   }
	    return jwtEncoder.encode(JwtEncoderParameters.from(claims.build())).getTokenValue();
	}

}
