package com.laraim237.restoX.modules.restaurant;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.laraim237.restoX.modules.menu.Category;
import com.laraim237.restoX.modules.menu.MenuItem;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
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
@Table(name = "restaurants")
public class Restaurant {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;
	
	private String description;
	
    private String address;
	
	private String logoUrl;
	
	@Column(length = 3)
	private String currency;
	
	@Column(nullable = false)
	private boolean active = true;
	
	@CreationTimestamp
	@Column(updatable = false)
	private Instant createdAt;

	@UpdateTimestamp
	@Column(nullable = false)
	private Instant updatedAt;
	
	@OneToMany(mappedBy = "restaurant", fetch = FetchType.LAZY)
	private List<RestaurantUser> restaurantUsers = new ArrayList<>();
	
	@OneToMany(mappedBy = "restaurant", fetch = FetchType.LAZY)
	private List<Category> categories = new ArrayList<>();

	@OneToMany(mappedBy = "restaurant", fetch = FetchType.LAZY)
	private List<MenuItem> menuItems = new ArrayList<>();
	
}
