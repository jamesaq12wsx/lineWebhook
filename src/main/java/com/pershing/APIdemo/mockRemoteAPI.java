package com.pershing.APIdemo;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class mockRemoteAPI {

	public static Map<String, UserData> users = new HashMap<String, UserData>();
	
	public static void validateUser(String userId) {
		if (users.containsKey(userId)) {
			users.get(userId).valid = true;
		}
	}
	
	public static boolean userValid(String userId) {
		if (users.containsKey(userId)) {
			return users.get(userId).valid;
		}
		return false;
	}
	
	// Assume input validation was already done by the calling application
	public static void setUserPhone(String userId, String phone) {
		if (!users.containsKey(userId)) {
			users.put(userId, new UserData());
			users.get(userId).balance = 100;
		}
		users.get(userId).phone = phone;
	}
	
	public static void setUserVerificationCode(String userId, String code) {
		if (!users.containsKey(userId)) {
			users.put(userId, new UserData());
			users.get(userId).balance = 100;
		}
		users.get(userId).code = code;
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
	
	// Mock getting users verification code from remote API
	public static String getUserVerificationCode(String userId) {
		if (users.containsKey(userId)) {
			return users.get(userId).code;
		} else {
			return "";
		}
	}
	
	public static void addUserPayee(String userId, String payee) {
		if (users.containsKey(userId)) {
			users.get(userId).payees.add(payee);
		}
	}
	
	// Mock getting user payees
	public static List<String> getUserPayees(String userId) {
		if (users.containsKey(userId)) {
			return users.get(userId).payees;
		} else {
			return new ArrayList<String>();
		}
	}
	
	// hehe taking in dem moneyzz
	public static void TakeUserMoney(String userId, int amount) {
		if (users.containsKey(userId)) {
			users.get(userId).balance -= amount;
		}
	}
	
}
