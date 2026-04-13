package com.laraim237.restoX.config.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.laraim237.restoX.entity.User;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UserDetailsImpl implements UserDetails {
	
	private final User user;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<>();
		
		//Role system
		if(user.getSystemRole() != null) {
			authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getSystemRole().name()));
		}
		
		//Role restaurant
		user.getRestaurantUsers().stream()
				.map(ru -> new SimpleGrantedAuthority("ROLE_" + ru.getRole().name()))
				.forEach(authorities::add);
		
		return authorities;
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}
	
	@Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

}
