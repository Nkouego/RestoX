package com.laraim237.restoX.modules.restaurant;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import com.laraim237.restoX.modules.user.User;
import com.laraim237.restoX.shared.enums.RestaurantRole;
import com.laraim237.restoX.shared.enums.StaffStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(name = "restaurant_user",
		uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "restaurant_id"}))
public class RestaurantUser {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private RestaurantRole role;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StaffStatus status;
	
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@ManyToOne
	@JoinColumn(name = "restaurant_id", nullable = false)
	private Restaurant restaurant;
	
	@CreationTimestamp
	@Column(updatable = false)
	private Instant assignedAt;
}
