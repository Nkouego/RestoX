package com.laraim237.restoX.service.Impl;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.laraim237.restoX.common.Exception.AccountAlreadyExistsException;
import com.laraim237.restoX.common.Exception.RestaurantAccessDeniedException;
import com.laraim237.restoX.common.Exception.OTPException;
import com.laraim237.restoX.common.Exception.ResourceNotFoundException;
import com.laraim237.restoX.common.utils.OtpUtils;
import com.laraim237.restoX.common.utils.StaffSpecification;
import com.laraim237.restoX.dto.StaffDto.AddStaffRequest;
import com.laraim237.restoX.dto.StaffDto.ConfirmInvitationRequest;
import com.laraim237.restoX.dto.StaffDto.ResendInviteRequest;
import com.laraim237.restoX.dto.StaffDto.StaffFilterRequest;
import com.laraim237.restoX.dto.StaffDto.StaffResponse;
import com.laraim237.restoX.dto.StaffDto.UpdateStaffRequest;
import com.laraim237.restoX.entity.AccessToken;
import com.laraim237.restoX.entity.Restaurant;
import com.laraim237.restoX.entity.RestaurantUser;
import com.laraim237.restoX.entity.User;
import com.laraim237.restoX.enums.AuditAction;
import com.laraim237.restoX.enums.StaffStatus;
import com.laraim237.restoX.enums.TokenType;
import com.laraim237.restoX.mapper.StaffMapper;
import com.laraim237.restoX.repository.AccessTokenRepository;
import com.laraim237.restoX.repository.RestaurantRepository;
import com.laraim237.restoX.repository.RestaurantUserRepository;
import com.laraim237.restoX.repository.UserRepository;
import com.laraim237.restoX.service.AuditService;
import com.laraim237.restoX.service.EmailService;
import com.laraim237.restoX.service.StaffService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class StaffServiceImpl implements StaffService{
	private final UserRepository userRepository;
	private final AccessTokenRepository accessTokenRepository;
	private final RestaurantUserRepository restaurantUserRepository;
	private final RestaurantRepository restaurantRepository;
	
	private final EmailService emailService;
	private final AuditService auditService;
	private final RestaurantSecurityService restaurantSecurityService;
	
	private final PasswordEncoder passwordEncoder;
	private final StaffMapper staffMapper;
	
	@Override
	public StaffResponse addStaff(AddStaffRequest request, Authentication authentication, String restaurantId, HttpServletRequest httpRequest) {
		User admin = restaurantSecurityService.getAuthenticatedUser(authentication);

	    Restaurant restaurant = restaurantRepository.findById(restaurantId)
		        .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
	    
		//2.On s'assure de recuperer l'utilisateur s'il a deja ete ajoute ou on le sauvegarde si il ne l'a pas encore ete
		User staff = userRepository.findByEmailIgnoreCase(request.email())
				.orElseGet(()-> {
					//3.ajoute le staff en base de donnees si utilisateur non trouvee
			        User newUser = staffMapper.toUser(request);
			        newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
			        return userRepository.save(newUser);
				});

		// 2.Vérifie si l'employée ajouté existe déjà dans ce restaurant
        if (restaurantUserRepository.existsByUserAndRestaurantAndRole(staff, restaurant, request.role())) {
            throw new AccountAlreadyExistsException("This staff member is already part of the restaurant.");
        }
        
        //4.Lis l'employée au restaurant de l'admin
        RestaurantUser restaurantUser = RestaurantUser.builder()
                .user(staff)
                .restaurant(restaurant)
                .role(request.role())
                .status(StaffStatus.PENDING)
                .build();
        restaurantUserRepository.save(restaurantUser);
        
        //5.Generer le token 
        AccessToken accessToken= createInvitationToken(staff, restaurantId);

        // 6. Envoyer l'email d'invitation à l'employée        
        emailService.sendStaffInvitationEmail(staff, accessToken, admin, restaurant);

        // 7. Audit
        auditService.log(AuditAction.STAFF_CREATED, staff.getId(),
            "staff", null, null, restaurantId, admin.getId(), httpRequest);

        StaffResponse response = staffMapper.toStaffResponse(staff, restaurantUser);
        
        return response.toBuilder()
            .message("Invitation sent")
            .build();
	}

	@Override
	public StaffResponse confirm(ConfirmInvitationRequest request, HttpServletRequest httpRequest, String restaurantId) {
		//1. trouve le token d'invitation
		
		AccessToken accessToken = accessTokenRepository.findByTokenAndType(request.token(), TokenType.INVITATION)
									.orElseThrow(()-> new OTPException("Invalid or expired invitation"));
		
		//2.On verifie l'expiration
		if(accessToken.isExpired()) {
			throw new OTPException(" expired invitation");
		}
		
		RestaurantUser restaurantUser= restaurantUserRepository.findByRestaurantIdAndUserId(restaurantId, accessToken.getUser().getId())
				.orElseThrow(() -> new RestaurantAccessDeniedException("Staff not linked to this restaurant"));
		

		if (restaurantUser.getStatus() != StaffStatus.PENDING) {
		    throw new IllegalStateException("Invitation cannot be confirmed. Current status: " 
		            + restaurantUser.getStatus());
		}
		
		//3.Recupere le user 
		User staff = accessToken.getUser();
		
		//4.Définit le mot de passe et active le compte
		staff.setPassword(passwordEncoder.encode(request.password()));
		staff.setEnabled(true);
		userRepository.save(staff);
		
		//5.on active le compte dans le restaurant
		restaurantUser.setStatus(StaffStatus.ACTIVE);
		restaurantUserRepository.save(restaurantUser);
		//6.supprimer le token
		accessTokenRepository.delete(accessToken);
		
		// 7. Audit
	    auditService.log(AuditAction.EMAIL_VERIFIED, staff.getId(),
	        "staff", null, null, restaurantId, staff.getId(), httpRequest);

	    return StaffResponse.builder()
	        .message("Account activated successfully. You can now login.")
	        .build();
	}

	@Override
	@Transactional(readOnly = true)
	public StaffResponse getById(String staffId, Authentication authentication, String restaurantId) {
		User staff = userRepository.findById(staffId)
						.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		
		RestaurantUser restaurantUser= restaurantUserRepository.findByRestaurantIdAndUserId(restaurantId, staff.getId())
												.orElseThrow(() -> new RestaurantAccessDeniedException("Staff not found"));
		StaffResponse staffResponse = staffMapper.toStaffResponse(staff, restaurantUser);
		
		return staffResponse;
	}

	@Override
	public StaffResponse update( UpdateStaffRequest request,
			                     String staffId, String restaurantId) {
		//recherche le staff
		User staff = userRepository.findById(staffId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		
		RestaurantUser restaurantUser= restaurantUserRepository.findByRestaurantIdAndUserId(restaurantId, staff.getId())
												.orElseThrow(() -> new RestaurantAccessDeniedException("Staff not found"));
		
		staffMapper.updateUser(request, staff);
		staffMapper.updateRestaurantUser(request, restaurantUser);
		restaurantUserRepository.save(restaurantUser);
		userRepository.save(staff);
        
        StaffResponse staffResponse = staffMapper.toStaffResponse(staff, restaurantUser);
		
		return staffResponse;
	}

	@Override
	public StaffResponse resendInvite(ResendInviteRequest request, String restaurantId, HttpServletRequest httpRequest, Authentication authentication) {
		User admin = restaurantSecurityService.getAuthenticatedUser(authentication);

		Restaurant restaurant = restaurantRepository.findById(restaurantId)
		        .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
		
		User staff = userRepository.findByEmailIgnoreCase(request.email())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		
		RestaurantUser restaurantUser = restaurantUserRepository
			    .findByRestaurantIdAndUserId(restaurantId, staff.getId())
			    .orElseThrow(() -> new RestaurantAccessDeniedException("Staff not found"));

			if (restaurantUser.getStatus() != StaffStatus.PENDING) {
			    throw new IllegalStateException(
			        "Cannot resend invitation. Staff status is: " + restaurantUser.getStatus()
			    );
			}
			
		//supprime l'ancien token s'il existe
		accessTokenRepository.deleteByUserAndType(staff, TokenType.INVITATION);
			
		 //.Genere un nouveau token 
		AccessToken accessToken = createInvitationToken(staff, restaurantId);

        // Envoyer l'email d'invitation à l'employée
        emailService.sendStaffInvitationEmail(staff, accessToken, admin, restaurant);
		
        return StaffResponse.builder()
                .message("Invitation resent")
                .build();
	}

	@Override
	public Page<StaffResponse> getAllStaff(String restaurantId, StaffFilterRequest filter) {
		//1.On cree une pagination
		Pageable pageable = PageRequest.of(filter.page(), filter.size());
	
		//3.On construit la specification
		Specification<RestaurantUser> specification = Specification.where(StaffSpecification.byRestaurant(restaurantId))
				.and(StaffSpecification.byStatus(filter.status()))
				.and(StaffSpecification.byName(filter.search()));
		
		return restaurantUserRepository.findAll(specification, pageable).map(restaurantUser -> 
																			staffMapper.toStaffResponse(restaurantUser.getUser(), restaurantUser));		
	}

	@Override
	public StaffResponse deactivate(String restaurantId, String staffId, Authentication authentication) {
		User admin = restaurantSecurityService.getAuthenticatedUser(authentication);
		
		RestaurantUser restaurantUser = getRestaurantUser(restaurantId, staffId);
        restaurantUser.setStatus(StaffStatus.INACTIVE);
        restaurantUserRepository.save(restaurantUser);
        
        User staff = restaurantUser.getUser();
        
        // Invalider le token d'invitation s'il existe encore
        accessTokenRepository.findByUserAndType(staff, TokenType.INVITATION)
            .ifPresent(accessTokenRepository::delete);

        auditService.log(AuditAction.STAFF_DEACTIVATED, staffId,
            "staff", null, null, restaurantId, admin.getId(), null);

        return staffMapper.toStaffResponse(staff, restaurantUser);
	}

	@Override
	public StaffResponse activate(String restaurantId, String staffId, Authentication authentication) {
		User admin = restaurantSecurityService.getAuthenticatedUser(authentication);
		
        RestaurantUser restaurantUser= getRestaurantUser(restaurantId, staffId);
        
        User staff = restaurantUser.getUser();
        
        restaurantUser.setStatus(StaffStatus.ACTIVE);
        restaurantUserRepository.save(restaurantUser);

        auditService.log(AuditAction.STAFF_ACTIVATED, staffId,
            "staff", null, null, restaurantId, admin.getId(), null);

        return staffMapper.toStaffResponse(staff, restaurantUser);
		
	}
	
    private RestaurantUser getRestaurantUser(String restaurantId, String staffId) {
        return restaurantUserRepository.findByRestaurantIdAndUserId(restaurantId, staffId)
            .orElseThrow(() -> new RestaurantAccessDeniedException("Staff not found"));
    }

    private AccessToken createInvitationToken(User staff, String restaurantId) {
        AccessToken token = OtpUtils.generateOTP(staff, TokenType.INVITATION, 1440);
        token.setRestaurantId(restaurantId);
        return accessTokenRepository.save(token);
    }

	}
