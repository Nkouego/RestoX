package com.laraim237.restoX.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laraim237.restoX.dto.StaffDto.AddStaffRequest;
import com.laraim237.restoX.dto.StaffDto.ConfirmInvitationRequest;
import com.laraim237.restoX.dto.StaffDto.ResendInviteRequest;
import com.laraim237.restoX.dto.StaffDto.StaffFilterRequest;
import com.laraim237.restoX.dto.StaffDto.StaffResponse;
import com.laraim237.restoX.dto.StaffDto.UpdateStaffRequest;
import com.laraim237.restoX.service.StaffService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/restaurants/{restaurantId}/staff")
public class StaffController{

	private final StaffService staffService;
	
	@PostMapping
	public ResponseEntity<StaffResponse> staffRegister(@PathVariable String restaurantId,  
			                                           @RequestBody @Valid AddStaffRequest request, 
			                                           Authentication authentication,
			                                           HttpServletRequest httpRequest) {
		return ResponseEntity.status(HttpStatus.CREATED)
				             .body(staffService.addStaff(request, authentication, restaurantId, httpRequest));
	}
	
	@PostMapping("/confirm")
	public ResponseEntity<StaffResponse> confirmInvitation(@PathVariable String restaurantId,
			                                               @RequestBody @Valid ConfirmInvitationRequest request, 
			                                               HttpServletRequest httpRequest){
		return ResponseEntity.ok(staffService.confirm(request, httpRequest, restaurantId));
	}
	
	@PostMapping("/resend-invite")
	public ResponseEntity<StaffResponse> resendInvite(@PathVariable String restaurantId,
			                                          @RequestBody @Valid ResendInviteRequest request,
			                                          HttpServletRequest httpRequest,
			                                          Authentication authentication){
		return ResponseEntity.ok(staffService.resendInvite(request, restaurantId, httpRequest, authentication));
	}
	
	@GetMapping("/{staffId}")
	public ResponseEntity<StaffResponse> getById(@PathVariable String staffId,
			                                     @PathVariable String restaurantId, 
			                                     Authentication authentication){
		return ResponseEntity.ok(staffService.getById(staffId, authentication, restaurantId));
	}
	
	@GetMapping
	public ResponseEntity<Page<StaffResponse>> getAllStaff( @PathVariable String restaurantId,
														 @ModelAttribute StaffFilterRequest filter){
		return ResponseEntity.ok(staffService.getAllStaff(restaurantId, filter));
		
	}
	
	@PutMapping("/{staffId}")
	public ResponseEntity<StaffResponse> update(@PathVariable String staffId, 
			                                    @PathVariable String restaurantId,
			                                    @RequestBody @Valid UpdateStaffRequest request){
		return ResponseEntity.ok(staffService.update(request, staffId, restaurantId));
	}
	
	@PatchMapping("/{staffId}/deactivate")
    public ResponseEntity<StaffResponse> deactivate(@PathVariable String restaurantId,
										            @PathVariable String staffId,
										            Authentication authentication) {
        return ResponseEntity.ok(staffService.deactivate(restaurantId, staffId, authentication));
	}

	@PatchMapping("/{staffId}/activate")
    public ResponseEntity<StaffResponse> activate( @PathVariable String restaurantId,
												   @PathVariable String staffId,
												   Authentication authentication) {
        return ResponseEntity.ok(staffService.activate(restaurantId, staffId, authentication));
     }
	


}
