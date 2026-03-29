package com.laraim237.restoX.modules.auth.service.Impl;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.laraim237.restoX.modules.auth.repository.UserRepository;
import com.laraim237.restoX.modules.user.User;
import com.laraim237.restoX.modules.user.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService{
	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByEmailIgnoreCase(username)
								.orElseThrow(()-> new UsernameNotFoundException("Email ou mot de passe incorrect"));
		return new UserDetailsImpl(user);
	}

}
