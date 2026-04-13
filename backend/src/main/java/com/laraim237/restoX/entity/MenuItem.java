package com.laraim237.restoX.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
@Table(name = "menu_items")
public class MenuItem{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String name;
	
	private String imageUrl;
	
	private String description;
	
	private boolean available=true;
	
	@Column(nullable = false)
	private BigDecimal price;
	
	@ManyToOne
	@JoinColumn(name = "restaurant_id", nullable = false)
	private Restaurant restaurant;
	
	@ManyToMany(mappedBy = "items")
	private List<Category> categories = new ArrayList<>();
	
	@OneToMany(mappedBy = "menuItem", fetch = FetchType.LAZY)
	private List<Variant> variants = new ArrayList<>();
	
	@CreationTimestamp
	@Column(updatable = false)
	private Instant createdAt;
	
	@UpdateTimestamp
	@Column(nullable = false)
	private Instant updatedAt;
	
}
