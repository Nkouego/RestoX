package com.laraim237.restoX.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "menu_items")
public class MenuItem{
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private String imageUrl;
	
	private String imagePublicId;
	
	private String description;
	
	@Builder.Default
	@Column(nullable = false)
	private boolean available=true;
	
	@Column(nullable = false)
	private BigDecimal price;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
	private Category category;
	
	@CreationTimestamp
	@Column(updatable = false)
	private Instant createdAt;
	
	private Instant deletedAt;
	
	@UpdateTimestamp
	@Column(nullable = false)
	private Instant updatedAt;
	
	@Builder.Default
	@OneToMany(mappedBy = "menuItem", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Variant> variants = new ArrayList<>();
	
	
}
