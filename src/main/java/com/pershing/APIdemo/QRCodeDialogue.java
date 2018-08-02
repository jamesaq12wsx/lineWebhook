package com.pershing.APIdemo;

import java.util.ArrayList;
import java.util.List;

import com.pershing.dialogue.Dialogue;
import com.pershing.event.MessageEvent;
import com.pershing.event.WebHookEvent;
import com.pershing.event.WebHookEventType;
import com.pershing.message.ImageMessage;
import com.pershing.message.Message;
import com.pershing.message.MessageType;
import com.pershing.message.TextMessage;
import com.pershing.util.Util;

public class QRCodeDialogue extends Dialogue {

	private String target;
	private int amount;
	
	public QRCodeDialogue(String userId) {
		Util.sendSingleTextPush(sender, userId, "Please enter the target of the QR code payment");
	}
	
	@Override
	public void handleEvent(WebHookEvent event, String userId) {
		if (event.type() == WebHookEventType.MESSAGE) {
			MessageEvent messageEvent = (MessageEvent) event;
			if (messageEvent.message().type() == MessageType.TEXT) {
				TextMessage textMessage = (TextMessage) messageEvent.message();
				if (target == null) {
					target = textMessage.getText();
					Util.sendSingleTextPush(sender, userId, "Please enter the transfer amount");
				} else {
					String amountString = textMessage.getText();
					try {
						amount = Integer.parseInt(amountString);
					} catch (Exception e) {
						Util.sendSingleTextPush(sender, userId, "Error occured when parsing: " + amountString);
						pop();
						return;
					}
					sendQRCodeMessage(userId);
					pop();
				}
			} else {
				pop();
			}
		} else {
			pop();
			this.top().handleEvent(event, userId);
		}
	}
	
	private final void sendQRCodeMessage(String userId) {
		String url = "https://peaceful-plains-74132.herokuapp.com/";
		url += '?';
		url += "target=" + target.replaceAll(" ", "%20");
		url += '&';
		url += "amount=" + Integer.toString(amount);
		List<Message> messages = new ArrayList<Message>();
		messages.add(new TextMessage("PAY: " + Integer.toString(amount) + " TO " + target));
		messages.add(new ImageMessage(url, url));
		sender.sendPush(userId, messages, "");
	}

}
