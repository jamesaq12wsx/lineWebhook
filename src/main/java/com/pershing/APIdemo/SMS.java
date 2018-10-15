package com.pershing.APIdemo;

import java.io.IOException;

import com.nexmo.client.NexmoClient;
import com.nexmo.client.NexmoClientException;
import com.nexmo.client.auth.AuthMethod;
import com.nexmo.client.auth.TokenAuthMethod;
import com.nexmo.client.sms.SmsSubmissionResult;
import com.nexmo.client.sms.messages.TextMessage;

/**
 * A utility class that handles SMS message sending
 * 
 * @author ianw3214
 *
 */
public class SMS {

	// The authentication data needed to use the Nexmo SMS api
	private static AuthMethod auth = new TokenAuthMethod("c9e258a1", "25xZsPalY6j1Pp90");
	private static NexmoClient client = new NexmoClient(auth);
	
	/**
	 * Sends a text message to a specified number
	 * 
	 * @param number	The number to send an SMS message to
	 * @param appName	The app name to display the SMS message under
	 * @param message	The message contained within the SMS message
	 */
	public static void sendTextMessage(String number, String appName, String message) {
		// First get the number to a valid format
		if (number.length() == 10) number = number.substring(1);
		if (number.length() == 9) number = "886" + number;
		// Then try to send the SMS message
		try {
			SmsSubmissionResult[] responses;
			responses = client.getSmsClient().submitMessage(new TextMessage(
			        appName,
			        number,
			        message));
			for (SmsSubmissionResult response : responses) {
			    System.out.println(response);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NexmoClientException e) {
			e.printStackTrace();
		}
	}
	
}
