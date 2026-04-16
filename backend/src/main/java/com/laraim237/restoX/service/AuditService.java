package com.laraim237.restoX.service;

import com.laraim237.restoX.enums.AuditAction;

import jakarta.servlet.http.HttpServletRequest;

public interface AuditService {
	void log(AuditAction action, Long entityId, String entityType,
			String oldValue, String newValue, 
			Long restaurantId, Long performedBy, HttpServletRequest request);
}
