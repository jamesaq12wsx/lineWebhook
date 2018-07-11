package com.pershing.APIdemo;

import com.pershing.action.MessageAction;
import com.pershing.dialogue.RootDialogue;
import com.pershing.event.FollowEvent;
import com.pershing.event.MessageEvent;
import com.pershing.event.WebHookEvent;
import com.pershing.event.WebHookEventType;
import com.pershing.message.MessageType;
import com.pershing.message.TemplateMessage;
import com.pershing.message.TextMessage;
import com.pershing.template.ButtonsTemplate;
import com.pershing.util.Util;

public class RootDialogueDemo extends RootDialogue {

	@Override
	public RootDialogue create() {
		return new RootDialogueDemo();
	}

	@Override
	public void handleEvent(WebHookEvent event, String userId) {
		if (event.type() == WebHookEventType.MESSAGE) {
			MessageEvent messageEvent = (MessageEvent) event;
			if (messageEvent.message().type() == MessageType.TEXT) {
				TextMessage textMessage = (TextMessage) messageEvent.message();
				parseTextMessage(textMessage.getText(), userId);
			}
		}
		if (event.type() == WebHookEventType.FOLLOW) {
			FollowEvent followEvent = (FollowEvent) event;
			// send a welcome message + a setup button
			TemplateMessage welcomeMessage = new TemplateMessage(
					"Welcome, type setup to get started.",
					new ButtonsTemplate.ButtonsTemplateBuilder(
							"Welcome, type setup to get started.").addAction(
									new MessageAction("setup", "setup")).build());
			Util.sendSingleReply(sender, followEvent.replyToken(), welcomeMessage);
		}
	}
	
	private void parseTextMessage(String text, String userId) {
		if (text.toLowerCase().contains("help")) {
			Util.sendSingleTextPush(sender, userId, "Type balance to check your balance");			
		} else if (text.toLowerCase().contains("setup")) {
			push(new SetupDialogue(userId));
		} else if (text.toLowerCase().contains("balance")) {
			// just print the response from within the root dialogue
			int balance = mockRemoteAPI.getUserBalance(userId);
			Util.sendSingleTextPush(sender, userId, "User balance: " + balance);
		}
	}

}
