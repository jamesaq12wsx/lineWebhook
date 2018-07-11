package com.pershing.APIdemo;

import java.util.Map;

public class mockRemoteAPI {

	public static Map<String, UserData> users;
	
	// Assume validation was already done by the calling application
	public static void setUserPhone(String userId, String phone) {
		if (!users.containsKey(userId)) {
			users.put(userId, new UserData());
		}
		users.get(userId).phone = phone;
		// temporarily just initialize balance to 100 dollars for now
		users.get(userId).balance = 100;
	}
	
	// Mock getting users balance from a remote API
	public static int getUserBalance(String userId) {
		if (users.containsKey(userId)) {
			return users.get(userId).balance;
		} else {
			return 0;
		}
	}
	
	// Mock getting users phone number from remote API
	public static String getUserPhone(String userId) {
		if (users.containsKey(userId)) {
			return users.get(userId).phone;
		} else {
			return "";
		}
	}
	
}
