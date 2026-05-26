package com.laraim237.restoX.common.Exception;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.laraim237.restoX.enums.AuditAction;
import com.laraim237.restoX.service.AuditService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalHandlerException {
	private final AuditService auditService;
	
	// 400 - Validation des champs (@Valid)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
		log.warn("conflict: {} ", ex.getMessage());
		List<Map<String, String>> fieldErrors = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(e-> Map.of("field", e.getField(), "message", e.getDefaultMessage()))
				.toList();
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid request body");
		problem.setTitle("Validation Error");
		problem.setInstance(URI.create(request.getRequestURI()));
		problem.setProperty("errors", fieldErrors);
		problem.setProperty("timestamp", Instant.now());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);	
	}
	
	// 404 - Ressource introuvable
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ProblemDetail> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
		log.warn("Resource not found{} ", ex.getMessage());
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Resource not found");
		problem.setTitle("Not Found");
		problem.setInstance(URI.create(request.getRequestURI()));
		problem.setProperty("timestamp", Instant.now());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
		
	}
	
	// 401 - Identifiants Incorrects
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ProblemDetail> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
		log.warn("Authentication failed {} ", ex.getMessage());
		
		//Audit
		auditService.log(AuditAction.LOGIN_FAILED, null, "User", null, null, null, null, request);
				
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Invalid email or password");
		problem.setTitle("Authentication failed");
		problem.setInstance(URI.create(request.getRequestURI()));
		problem.setProperty("timestamp", Instant.now());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem);
		
	}
			
	// 401 - Username introuvable
	@ExceptionHandler(RefreshTokenException.class)
	public ResponseEntity<ProblemDetail> handleRefreshToken(RefreshTokenException ex, HttpServletRequest request) {
		log.warn("Invalid Refresh Token {} ", ex.getMessage());
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
		problem.setTitle("Invalid Refresh Token");
		problem.setInstance(URI.create(request.getRequestURI()));
		problem.setProperty("timestamp", Instant.now());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem);
		
	}
	
	// 403 - compte desactivé
	@ExceptionHandler(DisabledException.class)
	public ResponseEntity<ProblemDetail> handleDisabled( DisabledException ex, HttpServletRequest request) {
	    log.warn("Disabled account: {}", ex.getMessage());
	    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Please confirm your email first");
	    problem.setTitle("Account not activated");
	    problem.setInstance(URI.create(request.getRequestURI()));
	    problem.setProperty("timestamp", Instant.now());
	    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problem);
	}
	
	
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ProblemDetail> handleConflict( DataIntegrityViolationException ex, HttpServletRequest request) {
		log.warn("Conflict resource: {}", ex.getMessage());
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Violation of data integrity");
		problem.setTitle("Conflict");
		problem.setInstance(URI.create(request.getRequestURI()));
		problem.setProperty("timestamp", Instant.now());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
	}
	
	@ExceptionHandler(AccountAlreadyExistsException.class)
	public ResponseEntity<ProblemDetail> handleConflict( AccountAlreadyExistsException ex, HttpServletRequest request) {
		log.warn("Conflict resource: {}", ex.getMessage());
	ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
		problem.setTitle("Conflict");
		problem.setInstance(URI.create(request.getRequestURI()));
		problem.setProperty("timestamp", Instant.now());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
	}
	
	@ExceptionHandler(OTPException.class)
	public ResponseEntity<ProblemDetail> handleOTP( OTPException ex, HttpServletRequest request) {
		log.warn("OTP error: {}", ex.getMessage());
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
		problem.setTitle("OTP error");
		problem.setInstance(URI.create(request.getRequestURI()));
		problem.setProperty("timestamp", Instant.now());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
	}
	
	@ExceptionHandler(StorageException.class)
	public ResponseEntity<ProblemDetail> handleStorage(StorageException ex, HttpServletRequest request) {
	    log.error("Storage error: {}", ex.getMessage());
	    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
	    problem.setTitle("Storage Error");
	    problem.setInstance(URI.create(request.getRequestURI()));
	    problem.setProperty("timestamp", Instant.now());
	    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(problem);
	}
		
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ProblemDetail> handleHttpMessageNotReadable( OTPException ex, HttpServletRequest request) {
		log.warn("OTP error: {}", "Invalid request body");
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid request body");
		problem.setTitle("Bad request");
		problem.setInstance(URI.create(request.getRequestURI()));
		problem.setProperty("timestamp", Instant.now());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
	}
	
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error", ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        problem.setTitle("Internal Server Error");
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("timestamp", LocalDateTime.now());
        return problem;
    }


}
