package com.laraim237.restoX.service;

import com.laraim237.restoX.enums.AuditAction;

import jakarta.servlet.http.HttpServletRequest;

public interface AuditService {
	void log(AuditAction action, String entityId, String entityType,
			String oldValue, String newValue, 
			String restaurantId, String performedBy, HttpServletRequest request);
}
