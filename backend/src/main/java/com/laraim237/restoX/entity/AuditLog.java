package com.laraim237.restoX.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.laraim237.restoX.enums.AuditAction;
import com.laraim237.restoX.enums.RestaurantRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
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
@Table(name = "audit_logs")
public class AuditLog {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AuditAction action;
	
	@Column(nullable = false)
	private String entityType;
	
	@Column(nullable = false)
	private String entityId;
	
	@Lob
	private String oldValue;
	
	@Lob
	private String newValue;
	
	@Column(name = "ip_address", length = 45)
	private String ipAddress;
	
	@Column(name = "user_agent", length = 512)
	private String userAgent;
	
	@ManyToOne
	@JoinColumn(name = "performed_by", nullable = false)
    private User performedBy;
	
	@ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = true)
    private Restaurant restaurant; // null = log plateforme, renseigné = log restaurant

	@CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Instant createdAt;
}
