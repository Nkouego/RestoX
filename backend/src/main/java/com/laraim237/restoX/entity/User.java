package com.laraim237.restoX.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.laraim237.restoX.enums.SystemRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User{
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	
	@Column(nullable = false)
	private String firstName;
	
	@Column(nullable = false)
	private String lastName;
	
	@Column(nullable = false, unique = true)
	private String email;
	
	@Column(nullable = false)
	private String password;
	
	private String pictureUrl;
	
	private String picturePublicId;
	
	@Enumerated(EnumType.STRING)
	private SystemRole systemRole; 
	
	@Column(nullable = false)
	@Builder.Default
	private boolean enabled = false;
	
	@CreationTimestamp
	@Column(updatable = false, nullable = false)
	private Instant createdAt;
	
	@UpdateTimestamp
	@Column(nullable = false)
	private Instant updatedAt;
	
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	@Builder.Default
	private List<RestaurantUser> restaurantUsers = new ArrayList<>();
	
	public String getFullName() {
	    return capitalize(firstName) + " " + capitalize(lastName);
	}
	
	private String capitalize(String value) {
	    return value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase();
	}
}
