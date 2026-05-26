package com.laraim237.restoX.service;

import com.laraim237.restoX.entity.AccessToken;
import com.laraim237.restoX.entity.Restaurant;
import com.laraim237.restoX.entity.User;

public interface EmailService {
	void sendVerificationEmail(User user, AccessToken accessToken);
    void sendPasswordResetEmail(User user, AccessToken accessToken);
    void sendStaffInvitationEmail(User employee, AccessToken accessToken, User admin, Restaurant restaurant);
    void sendWelcomeEmail(User user);
}
