package com.pershing.APIdemo;

import com.pershing.action.MessageAction;
import com.pershing.dialogue.Dialogue;
import com.pershing.event.MessageEvent;
import com.pershing.event.WebHookEvent;
import com.pershing.event.WebHookEventType;
import com.pershing.message.MessageType;
import com.pershing.message.TemplateMessage;
import com.pershing.message.TextMessage;
import com.pershing.mockAPI.MockAPI;
import com.pershing.template.ButtonsTemplate;
import com.pershing.util.Util;

public class RatesDialogue extends Dialogue {

	boolean unitSet;
	String unitCode;
	
	public RatesDialogue(String userId) {
		unitSet = false;
		ButtonsTemplate buttons = new ButtonsTemplate.ButtonsTemplateBuilder(
				"Please enter the country code for the final currency unit")
				.addAction(new MessageAction("TWD", "TWD"))
				.build();
		TemplateMessage message = new TemplateMessage("Please enter the country code for the final currency unit", buttons);
		Util.sendSinglePush(sender, userId, message);	
	}
	
	@Override
	public void handleEvent(WebHookEvent event, String userId) {
		if (event.type() == WebHookEventType.MESSAGE) {
			MessageEvent messageEvent = (MessageEvent) event;
			if (messageEvent.message().type() == MessageType.TEXT) {
				TextMessage textMessage = (TextMessage) messageEvent.message();
				if (!unitSet) {
					unitCode = textMessage.getText();
					ButtonsTemplate buttons = new ButtonsTemplate.ButtonsTemplateBuilder(
							"Please enter the country code for the currency to convert from")
							.addAction(new MessageAction("CAD", "CAD"))
							.build();
					TemplateMessage message = new TemplateMessage("Please enter the country code for the currency to convert from", buttons);
					Util.sendSinglePush(sender, userId, message);
					unitSet = true;
				} else {
					String source = textMessage.getText();
					// send a HTTP request to find the currency
					float result = MockAPI.getCurrency(unitCode, source);
					String message = "1 " + source + " = " + Float.toString(result) + " " + unitCode;
					Util.sendSingleTextPush(sender, userId, message);
					pop();
				}
			}
		}
	}

}
