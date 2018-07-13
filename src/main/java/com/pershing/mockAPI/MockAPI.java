package com.pershing.mockAPI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MockAPI {

	public static Map<String, UserData> users = new HashMap<String, UserData>();
	
	// Generate an account, used for testing purposes only
	// Account must be generated with a phone number
	public static void generateAccount(String userId, String phone) {
		if (!users.containsKey(userId)) {
			UserData data = new UserData();
			data.phone = phone;
			data.accounts.add(new Account(1000, "Checking", UUID.randomUUID().toString()));
			data.accounts.add(new Account(1000, "Savings", UUID.randomUUID().toString()));
		}
	}
	
	public static List<Account> getUserAccounts(String userId) {
		return users.get(userId).accounts;
	}
	
	public static boolean userValid(String userId) {
		return users.containsKey(userId) ? users.get(userId).valid : false;
	}
	
	public static void validateUser(String userId) {
		if (users.containsKey(userId)) users.get(userId).valid = true;
	}
	
}
