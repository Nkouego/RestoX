package com.laraim237.restoX.entity;

import java.time.Duration;
import java.time.Instant;

import com.laraim237.restoX.enums.TokenType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "access_token")
public class AccessToken{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@Column(nullable = false, unique = true)
	private String token;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TokenType type;
	
	@Column(nullable = false)
	private Instant expiredAt;
	
	public AccessToken(String token, User user, int durationInMinutes, TokenType type) {
        this.token = token;
        this.user = user;
        this.type = type;
        this.expiredAt = Instant.now().plus(Duration.ofMinutes(durationInMinutes));
	}
	
	public long getRemainingMinutes() {
	    Duration duration  = Duration.between(Instant.now(), this.expiredAt);
	    if (duration.isNegative())  return 0;
	    return (long) Math.ceil(duration.getSeconds() / 60.0); 
	}
	
	public boolean isExpired() {
		return Instant.now().isAfter(this.expiredAt);
	}
}
