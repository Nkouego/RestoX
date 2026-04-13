package com.laraim237.restoX.service;

import com.laraim237.restoX.entity.AccessToken;
import com.laraim237.restoX.entity.User;

public interface EmailService {
	void sendVerificationEmail(User user, AccessToken accessToken);
    void sendPasswordResetEmail(User user, AccessToken accessToken);
    void sendStaffInvitationEmail(User user, AccessToken accessToken);
    void sendWelcomeEmail(User user);
}
