package com.laraim237.restoX.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.laraim237.restoX.entity.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByEmail(String email);
	Optional<User> findByEmailIgnoreCase(String email);

}
