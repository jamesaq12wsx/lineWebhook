package com.pershing.APIdemo;

import java.util.Random;

import com.pershing.dialogue.Dialogue;
import com.pershing.event.MessageEvent;
import com.pershing.event.WebHookEvent;
import com.pershing.event.WebHookEventType;
import com.pershing.message.MessageType;
import com.pershing.message.TextMessage;
import com.pershing.util.Util;

public class VerifyPhoneDialogue extends Dialogue {
	
	private int triesLeft;

	public VerifyPhoneDialogue(String userId) {
		String code = getFourDigitCode();
		Util.sendSingleTextPush(sender, userId, "A 4 digit code has been sent to your phone, please enter it below for validation.");
		Util.sendSingleTextPush(sender, userId, "SMS message sending disabled for now, the code is: " + code);
		mockRemoteAPI.setUserVerificationCode(userId, code);
		String number = mockRemoteAPI.getUserPhone(userId);
		// SMS.sendTextMessage(number, "DEMO", "Your code is: " + code);
		triesLeft = 3;
	}
	
	@Override
	public void handleEvent(WebHookEvent event, String userId) {
		if (event.type() == WebHookEventType.MESSAGE) {
			MessageEvent messageEvent = (MessageEvent) event;
			if (messageEvent.message().type() == MessageType.TEXT) {
				TextMessage textMessage = (TextMessage) messageEvent.message();
				// see if the message matches
				String code = mockRemoteAPI.getUserVerificationCode(userId);
				if (textMessage.getText().equals(code)) {
					Util.sendSingleTextReply(sender,
							messageEvent.replyToken(),
							"Code verified, validating user account.");
					mockRemoteAPI.validateUser(userId);
					pop();
				} else {
					triesLeft--;
					if (triesLeft <= 0) pop();
					Util.sendSingleTextReply(sender, 
							messageEvent.replyToken(), 
							"Incorrect code, tries remaining: " + Integer.toString(triesLeft));
				}
			}
		}
	}
	
	private final String getFourDigitCode() {
		Random rand = new Random();
		int digit1 = rand.nextInt(9);
		int digit2 = rand.nextInt(9);
		int digit3 = rand.nextInt(9);
		int digit4 = rand.nextInt(9);
		return Integer.toString(digit1) + Integer.toString(digit2) +
				Integer.toString(digit3) + Integer.toString(digit4);
	}
	
}
