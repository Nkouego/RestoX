package com.laraim237.restoX.service;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

import com.laraim237.restoX.dto.StaffDto.AddStaffRequest;
import com.laraim237.restoX.dto.StaffDto.ConfirmInvitationRequest;
import com.laraim237.restoX.dto.StaffDto.ResendInviteRequest;
import com.laraim237.restoX.dto.StaffDto.StaffFilterRequest;
import com.laraim237.restoX.dto.StaffDto.StaffResponse;
import com.laraim237.restoX.dto.StaffDto.UpdateStaffRequest;

import jakarta.servlet.http.HttpServletRequest;

public interface StaffService {

	StaffResponse addStaff(AddStaffRequest request, Authentication authentication, String restaurantId, HttpServletRequest httpRequest);

	StaffResponse confirm(ConfirmInvitationRequest request, HttpServletRequest httpRequest, String restaurantId);

	StaffResponse getById(String staffId, Authentication authentication, String restaurantId);

	StaffResponse update(UpdateStaffRequest request,String staffId, String restaurantId);

	StaffResponse resendInvite(ResendInviteRequest request, String restaurantId, HttpServletRequest httpRequest, Authentication authentication);

	Page<StaffResponse> getAllStaff(String restaurantId, StaffFilterRequest filter);

	StaffResponse deactivate(String restaurantId, String staffId, Authentication authentication);

	StaffResponse activate(String restaurantId, String staffId, Authentication authentication);
}