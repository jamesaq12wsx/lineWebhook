package com.pershing.dialogue;

import java.util.ArrayList;
import java.util.List;

import com.pershing.event.MessageEvent;
import com.pershing.event.WebHookEvent;
import com.pershing.event.WebHookEventType;
import com.pershing.message.Message;
import com.pershing.message.MessageType;
import com.pershing.message.TextMessage;

public class EchoRootDialogue extends RootDialogue {

	@Override
	public RootDialogue create() {
		return new EchoRootDialogue();
	}

	@Override
	public void handleEvent(WebHookEvent event, String userId) {
		if (event.type() == WebHookEventType.MESSAGE) {
			// only respond to message events with the echo bot
			List<Message> reply = new ArrayList<Message>();
			MessageEvent messageEvent = (MessageEvent) event;
			if (messageEvent.message().type() == MessageType.TEXT) {
				TextMessage textMessage = (TextMessage) messageEvent.message();
				reply.add(new TextMessage(textMessage.getText()));
			} else {
				reply.add(new TextMessage("MESSAGE RECIEVED: " + messageEvent.message().type().toString()));
			}
			String replyToken = messageEvent.replyToken();
			sender.sendReply(replyToken, reply, "");
		}
	}

}
