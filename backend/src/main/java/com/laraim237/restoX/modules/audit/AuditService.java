package com.laraim237.restoX.modules.audit;

import jakarta.servlet.http.HttpServletRequest;

public interface AuditService {
	void log(AuditAction action, Long entityId, String entityType,
			String oldValue, String newValue, 
			Long restaurantId, Long performedBy, HttpServletRequest request);
}
