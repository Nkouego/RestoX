package com.laraim237.restoX.service.Impl;

import org.springframework.stereotype.Service;

import com.laraim237.restoX.entity.AuditLog;
import com.laraim237.restoX.entity.Restaurant;
import com.laraim237.restoX.entity.User;
import com.laraim237.restoX.enums.AuditAction;
import com.laraim237.restoX.repository.AuditLogRepository;
import com.laraim237.restoX.service.AuditService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

	private final AuditLogRepository auditLogRepository;
	
	@Override
	public void log(AuditAction action, String entityId, String entityType, String oldValue, String newValue,
		String restaurantId, String performedBy, HttpServletRequest request) {
		
		AuditLog auditLog = AuditLog.builder()
									.action(action)
									.entityId(entityId)
									.entityType(entityType)
									.restaurant(restaurantId != null?
											Restaurant.builder().id(restaurantId).build() : null)
									.performedBy(User.builder().id(performedBy).build())
									.newValue(newValue)
									.oldValue(oldValue)
									.ipAddress(getClientId(request))
									.userAgent(request.getHeader("User-Agent"))
									.build();
		
		auditLogRepository.save(auditLog);
	}

	private String getClientId(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if(ip != null && !ip.isEmpty()) {
			return ip = ip.split(",")[0].trim();
		}
		return  request.getRemoteAddr();
	}

}
