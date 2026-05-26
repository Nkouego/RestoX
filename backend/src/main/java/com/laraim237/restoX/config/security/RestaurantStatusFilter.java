package com.laraim237.restoX.config.security;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.laraim237.restoX.common.utils.JwtUtils;
import com.laraim237.restoX.entity.Restaurant;
import com.laraim237.restoX.enums.RestaurantStatus;
import com.laraim237.restoX.repository.RestaurantRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Component
@Slf4j
@RequiredArgsConstructor
public class RestaurantStatusFilter extends OncePerRequestFilter {
	
	private final ObjectMapper mapper;
	private final RestaurantRepository restaurantRepository;
	private final JwtUtils jwtUtils;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		//1. On extrait l'id du restaurant de l'url
		String restaurantId = extractRestaurantId(request.getRequestURI());
		
		// 2. Si pas de restaurantId dans l'URL, on laisse passer la requete
		if (restaurantId == null) {
            filterChain.doFilter(request, response);
            return;
        }
		
		// 3. Extraire currentRestaurantId du JWT
        String currentRestaurantId = jwtUtils.getCurrentRestaurantId(
            (Authentication) SecurityContextHolder.getContext().getAuthentication()
        );
		
        //4.Pas encore switché, bloquer
        if(currentRestaurantId == null) {
        	sendError(request, response, HttpStatus.FORBIDDEN, "Please select a restaurant");
        	return;
        }
        
       // 5. Token ne correspond pas à l'URL, on bloque
        if (!currentRestaurantId.equals(restaurantId)) {
            sendError(request, response, HttpStatus.FORBIDDEN, "Access denied to this restaurant");
            return;
        }
        
		//4.on recupere le restaurant en question
		Restaurant restaurant = restaurantRepository.findById(restaurantId)
				.orElse(null);
		
		 if (restaurant == null) {	
			 sendError(request, response, HttpStatus.NOT_FOUND, "Restaurant not found");
	         return;
	        }
		

        // 5. Si ARCHIVED, on bloque tout
        if (restaurant.getStatus() == RestaurantStatus.ARCHIVED) {
        	sendError(request, response, HttpStatus.FORBIDDEN, "This restaurant is archived");
            return;
        }
        
        //6.INACTIF, on bloque sauf l'endpoint d'activation
        if(restaurant.getStatus() == RestaurantStatus.INACTIVE) {
        	String method = request.getMethod();
        	String uri = request.getRequestURI();
        	
        	if(!(method.equals("PATCH") && uri.endsWith("/activate"))) {
        		sendError(request, response, HttpStatus.FORBIDDEN, "This restaurant is inactive");
                return;
        	}
        }
        
        // 7. Tout est ok, on laisse passer
        filterChain.doFilter(request, response);	
	}

	private void sendError(HttpServletRequest request, HttpServletResponse response,
	            HttpStatus status, String message) throws IOException {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, message);
		problem.setTitle(status.getReasonPhrase());
		problem.setInstance(URI.create(request.getRequestURI()));
		problem.setProperty("timestamp", Instant.now());
		
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(status.value());
		mapper.writeValue(response.getOutputStream(), problem);
	}

	private String extractRestaurantId(String uri) {
		String[] parts = uri.split("/");
		for (int i = 0; i < parts.length-1; i++) {
			if(parts[i].equals("restaurants")) {
				String next = parts[i+1];
				if(next.matches("[0-9a-fA-F-]{36}")) {
					return next;
				}
			}
		}
		return null;
	}

}
