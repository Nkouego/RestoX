package com.laraim237.restoX.modules.auth.service.Impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.laraim237.restoX.modules.auth.service.JwtService;
import com.laraim237.restoX.modules.user.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

	private final JwtEncoder jwtEncoder;

    @Value("${spring.security.jwt.expiration}")
    private long expiration;

	@Override
	public String generateToken(User user) {
		Instant now = Instant.now();
		
		  // Collecte tous les rôles (system + restaurant)
	    List<String> roles = new ArrayList<>();
	    if(user.getSystemRole() != null) {
	    	roles.add("ROLE_"+user.getSystemRole().name());
	    }
	    
	    user.getRestaurantUsers().forEach(ru->{
	    	roles.add("ROLE_"+ru.getRole().name());
	    });
	    
	    //collecte tout les restaurants auquels appartiennent un utilisateur
	    List<Long> restaurantIds = user.getRestaurantUsers().stream()
	    		.map(ru-> ru.getRestaurant().getId())
	    		.toList();
	    
	    
		//Definit les claims du jwt
	    JwtClaimsSet claims = JwtClaimsSet.builder()
	    		.issuer("restoX")
	    		.subject(user.getEmail())
	    		.claim("userId", user.getId())
	    		.claim("roles", roles)
	    		.claim("restaurants", restaurantIds)
	    		.issuedAt(now)
	    		.expiresAt(now.plusSeconds(expiration))
	    		.build();
	    
	    return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
	}

}
