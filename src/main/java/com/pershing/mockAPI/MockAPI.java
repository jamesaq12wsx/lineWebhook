package com.pershing.mockAPI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * A simulation of a bank API that would be used in conjunction with a bank bot
 * 
 * This package should not be used anymore now that a chatbot backend has been developed
 * 
 * @author ianw3214
 *
 */
public class MockAPI {

	public static Map<String, UserData> users = new HashMap<String, UserData>();
	
	// Generate an account, used for testing purposes only
	// Account must be generated with a phone number
	public static void generateAccount(String userId, String phone) {
		if (!users.containsKey(userId)) {
			UserData data = new UserData();
			data.phone = phone;
			data.accounts.add(new Account(1000, "Chequing Account", UUID.randomUUID().toString()));
			data.accounts.add(new Account(1000, "Savings Account", UUID.randomUUID().toString()));
			users.put(userId, data);
		}
	}
	
	public static UserData getUserInfo(String userId) {
		return users.get(userId);
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
	
	public static void userPayment(String userId, int amount, String target) {
		if (users.containsKey(userId)) {
			if (users.get(userId).accounts.size() <= 0) return;
			Account account = users.get(userId).accounts.get(0);
			account.balance -= amount;
			account.history.add(Integer.toString(amount) + " payed to '" + target + '\'');
			// now also update the payee information
			for (Payee payee : users.get(userId).payees) {
				if (payee.name.equals(target)) {
					payee.history.add(Integer.toString(amount));
					return;
				}
			}
			// If the payee wasn't found, add it to the list
			users.get(userId).payees.add(new Payee(target));
		}
	}
	
	public static List<Payee> getUserPayees(String userId) {
		return users.get(userId).payees;
	}
	
	public static float getCurrency(String unit, String source) {
		try {
			String url = "https://free.currencyconverterapi.com/api/v6/convert?q=";
			url += source + "_" + unit + "&compact=ultra";
		
			HttpClient client = HttpClients.createDefault();
			HttpGet request = new HttpGet(url);
		
			HttpResponse response = client.execute(request);
		
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + 
		                   response.getStatusLine().getStatusCode());
		
			BufferedReader rd = new BufferedReader(
		                   new InputStreamReader(response.getEntity().getContent()));
		
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			
			JsonParser parser = new JsonParser();
			JsonObject resultObject = parser.parse(result.toString()).getAsJsonObject();
			
			String val = source + "_" + unit;
			if (resultObject.get(val) != null) {
				return resultObject.get(val).getAsFloat();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0.f;
	}
	
}
