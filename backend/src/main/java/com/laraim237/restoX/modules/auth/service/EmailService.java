package com.laraim237.restoX.modules.auth.service;

import com.laraim237.restoX.modules.auth.entity.AccessToken;
import com.laraim237.restoX.modules.user.User;

public interface EmailService {
	void sendVerificationEmail(User user, AccessToken accessToken);
    void sendPasswordResetEmail(User user, AccessToken accessToken);
    void sendStaffInvitationEmail(User user, AccessToken accessToken);
    void sendWelcomeEmail(User user);
}
