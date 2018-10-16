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

/**
 * A demo dialogue that querys country information from a user and responds with exchange rate
 * 
 * @author ianw3214
 *
 */
public class RatesDialogue extends Dialogue {

	// Flag indicating whether the unit code of the country has been set
	private boolean unitSet;
	// The unit code of the country to find exchange rates for
	private String unitCode;
	
	/**
	 * Constructor of the rates dialogue which sends a prompt to the user
	 * @param userId	The user that the rates dialogue was pushed onto
	 */
	public RatesDialogue(String userId) {
		unitSet = false;
		// Return a default country to get the exchange rate for (Taiwan Dollars, TWD)
		ButtonsTemplate buttons = new ButtonsTemplate.ButtonsTemplateBuilder(
				"Please enter the country code for the final currency unit")
				.addAction(new MessageAction("TWD", "TWD"))
				.build();
		TemplateMessage message = new TemplateMessage("Please enter the country code for the final currency unit", buttons);
		Util.sendSinglePush(sender, userId, message);	
	}
	
	/**
	 * The general event handler function that contains the logic of the rates dialogue
	 */
	@Override
	public void handleEvent(WebHookEvent event, String userId) {
		// Only text messages matter for rates dialogue
		if (event.type() == WebHookEventType.MESSAGE) {
			MessageEvent messageEvent = (MessageEvent) event;
			if (messageEvent.message().type() == MessageType.TEXT) {
				TextMessage textMessage = (TextMessage) messageEvent.message();
				// Set the unit if it is not yet set
				if (!unitSet) {
					unitCode = textMessage.getText();
					ButtonsTemplate buttons = new ButtonsTemplate.ButtonsTemplateBuilder(
							"Please enter the country code for the currency to convert from")
							.addAction(new MessageAction("CAD", "CAD"))
							.build();
					TemplateMessage message = new TemplateMessage("Please enter the country code for the currency to convert from", buttons);
					Util.sendSinglePush(sender, userId, message);
					unitSet = true;
				// Get the exchange rate of the input country and unit country and respond w/ the result
				} else {
					String source = textMessage.getText();
					// Query the currency value from the mock API
					float result = MockAPI.getCurrency(unitCode, source);
					String message = "1 " + source + " = " + Float.toString(result) + " " + unitCode;
					Util.sendSingleTextPush(sender, userId, message);
					pop();
				}
			}
		}
	}

}
