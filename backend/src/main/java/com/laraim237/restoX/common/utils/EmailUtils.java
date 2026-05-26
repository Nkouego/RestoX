package com.laraim237.restoX.common.utils;

public class EmailUtils {
	
	public static String getInvitationUrl(String host, String token) {
		return host+"/confirm-invitation?token="+token;
	}

}
