package com.laraim237.restoX.modules.auth.service.Impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.laraim237.restoX.modules.auth.entity.AccessToken;
import com.laraim237.restoX.modules.auth.service.EmailService;
import com.laraim237.restoX.modules.user.User;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService{
	private static final String UTF_8_ENCODING = "utf-8";
	private final JavaMailSender emailSender;
	private final TemplateEngine templateEngine;
	
	@Value("${spring.mail.username}")
	private String fromEmail;
	@Value("${spring.mail.from.name}")
	private String fromName;

	@Override
	@Async
	public void sendVerificationEmail(User user, AccessToken accessToken) {
		try {
			Context context = new Context();
			context.setVariable("user", user);
			context.setVariable("accessToken", accessToken);
			
			String text = templateEngine.process("VerificationEmail", context);
			
			MimeMessage message = emailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING); 
			helper.setSubject("Your verification code is " + accessToken.getToken());
			helper.setFrom(fromEmail, fromName);
			helper.setTo(user.getEmail());
			helper.setText(text, true);
			
			emailSender.send(message);
		} catch (Exception e) {
			log.error("Failed to send confirmation email to: {}", user.getEmail(), e);
			throw new RuntimeException("Failed to send email", e);
		}
		
		
	}

	@Override
	public void sendWelcomeEmail(User user) {

		try {
			Context context = new Context();
			context.setVariable("user", user);
			
			String text = templateEngine.process("WelcomeEmail", context);
			
			MimeMessage message = emailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING); 
			helper.setSubject("Welcome to RestoX");
			helper.setFrom(fromEmail, fromName);
			helper.setTo(user.getEmail());
			helper.setText(text, true);
			
			emailSender.send(message);
		} catch (Exception e) {
			log.error("Failed to send confirmation email to: {}", user.getEmail(), e);
			throw new RuntimeException("Failed to send email", e);
		}
		
	}

	@Override
	@Async
	public void sendPasswordResetEmail(User user, AccessToken accessToken) {
		try {
			Context context = new Context();
			context.setVariable("user", user);
			context.setVariable("accessToken", accessToken);
			
			String text = templateEngine.process("ResetPasswordEmail", context);
			
			MimeMessage message = emailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING); 
			helper.setSubject("Your reset password code is " + accessToken.getToken());
			helper.setFrom(fromEmail, fromName);
			helper.setTo(user.getEmail());
			helper.setText(text, true);
			
			emailSender.send(message);
		} catch (Exception e) {
			log.error("Failed to send confirmation email to: {}", user.getEmail(), e);
			throw new RuntimeException("Failed to send email", e);
		}
		
	}

	@Override
	public void sendStaffInvitationEmail(User user, AccessToken accessToken) {
		// TODO Auto-generated method stub
		
	}

}
