package com.pershing.dialogue;

import java.util.ArrayList;
import java.util.List;

import com.pershing.event.MessageEvent;
import com.pershing.event.WebHookEvent;
import com.pershing.event.WebHookEventType;
import com.pershing.message.Message;
import com.pershing.message.MessageType;
import com.pershing.message.TextMessage;

/**
 * Example Dialogue implementation that echoes incoming text messages
 * 
 * @author ianw3214
 *
 */
public class EchoRootDialogue extends RootDialogue {

	/**
	 * The create function is required for root dialogues, and in this case it
	 * 	simply returns a new instance of the class.
	 */
	@Override
	public RootDialogue create() {
		return new EchoRootDialogue();
	}

	@Override
	public void handleEvent(WebHookEvent event, String userId) {
		// only respond to text message events with the echo bot
		if (event.type() == WebHookEventType.MESSAGE) {
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
