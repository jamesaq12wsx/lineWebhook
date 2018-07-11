package com.pershing.APIdemo;

import java.io.IOException;

import com.nexmo.client.NexmoClient;
import com.nexmo.client.NexmoClientException;
import com.nexmo.client.auth.AuthMethod;
import com.nexmo.client.auth.TokenAuthMethod;
import com.nexmo.client.sms.SmsSubmissionResult;
import com.nexmo.client.sms.messages.TextMessage;

public class SMS {

	private static AuthMethod auth = new TokenAuthMethod("c9e258a1", "25xZsPalY6j1Pp90");
	private static NexmoClient client = new NexmoClient(auth);
	
	public static void sendTextMessage(String number, String appName, String message) {
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
