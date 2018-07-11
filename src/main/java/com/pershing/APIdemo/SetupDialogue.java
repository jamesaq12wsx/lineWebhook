package com.pershing.APIdemo;

import com.pershing.action.MessageAction;
import com.pershing.dialogue.Dialogue;
import com.pershing.event.MessageEvent;
import com.pershing.event.WebHookEvent;
import com.pershing.event.WebHookEventType;
import com.pershing.message.MessageType;
import com.pershing.message.TemplateMessage;
import com.pershing.message.TextMessage;
import com.pershing.template.ButtonsTemplate;
import com.pershing.util.Util;

public class SetupDialogue extends Dialogue {

	String userId;
	
	// constructor of the dialogue
	public SetupDialogue(String userId) {
		this.userId = userId;
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
				if (phone.length() == 10) {
					phone = phone.substring(1);
				}
				if (phone.length() == 9) {
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
		// send a message based on whether the phone was verified or not
		if (mockRemoteAPI.userValid(userId)) {
			TemplateMessage menu = new TemplateMessage(
					"User validated, type help for different commands.",
					new ButtonsTemplate.ButtonsTemplateBuilder(
							"User validated, type help for different commands.")
					.addAction(new MessageAction("help", "help"))
					.addAction(new MessageAction("balance", "balance"))
					.build());
			Util.sendSinglePush(sender, userId, menu);
		} else {
			Util.sendSingleTextPush(sender, userId, "User validation failed, type setup to try again.");
		}
		pop();
	}
	
}
