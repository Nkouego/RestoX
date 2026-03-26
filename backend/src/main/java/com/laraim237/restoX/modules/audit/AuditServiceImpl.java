package com.laraim237.restoX.modules.audit;

import org.springframework.stereotype.Service;

import com.laraim237.restoX.modules.restaurant.Restaurant;
import com.laraim237.restoX.modules.user.User;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

	private final AuditLogRepository auditLogRepository;
	
	@Override
	public void log(AuditAction action, Long entityId, String entityType, String oldValue, String newValue,
			Long restaurantId, Long performedBy, HttpServletRequest request) {
		
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
