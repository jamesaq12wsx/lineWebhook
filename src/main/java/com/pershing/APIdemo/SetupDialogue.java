package com.pershing.APIdemo;

import com.pershing.dialogue.Dialogue;
import com.pershing.event.MessageEvent;
import com.pershing.event.WebHookEvent;
import com.pershing.event.WebHookEventType;
import com.pershing.message.MessageType;
import com.pershing.message.TextMessage;
import com.pershing.util.Util;

public class SetupDialogue extends Dialogue {

	// constructor of the dialogue
	public SetupDialogue(String userId) {
		// send a push message to prompt for phone number to start the setup process
		Util.sendSingleTextPush(sender, userId, 
				"Please start the setup process by typing your phone number.");
	}
	
	@Override
	public void handleEvent(WebHookEvent event, String userId) {
		if (event.type() == WebHookEventType.MESSAGE) {
			MessageEvent messageEvent = (MessageEvent) event;
			if (messageEvent.message().type() == MessageType.TEXT) {
				TextMessage textMessage = (TextMessage) messageEvent.message();
				String phone = textMessage.getText();
				if (!(phone.charAt(0) == '8' && phone.charAt(1) == '8' && phone.charAt(2) == '6')) {
					phone = "886" + phone;
				}
				// TODO: Do some text validation
				mockRemoteAPI.setUserPhone(userId, phone);
				// Push a verification code dialogue to the user
				push(new VerifyPhoneDialogue(userId));
			}
		}
	}
	
	@Override
	public void recieve() {
		// just pop and don't do anymore verification for now
		pop();
	}
	
}
