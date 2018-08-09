package com.pershing.APIdemo;

import java.util.List;

import com.pershing.action.MessageAction;
import com.pershing.dialogue.Dialogue;
import com.pershing.event.MessageEvent;
import com.pershing.event.WebHookEvent;
import com.pershing.event.WebHookEventType;
import com.pershing.message.MessageType;
import com.pershing.message.TemplateMessage;
import com.pershing.message.TextMessage;
import com.pershing.mockAPI.MockAPI;
import com.pershing.mockAPI.Payee;
import com.pershing.template.ButtonsTemplate;
import com.pershing.template.ConfirmTemplate;
import com.pershing.util.Util;

/**
 * A demo dialogue that querys billing information from a user and sends a billing payment
 * 	to a mock remote API
 * 
 * @author ianw3214
 *
 */
public class BillingDialogue extends Dialogue {

	// Flag indicating whether the payment target has been set
	private boolean targetSet;
	// The target of the payment
	private String target;
	// Flag indicating whether the payment amount has been set
	private boolean amountSet;
	// The amount of the payment
	private int amount;
	
	/**
	 * Constructor of billing dialogue which sends a prompt to the user
	 * @param userId	The user that the billing dialogue was pushed onto
	 */
	public BillingDialogue(String userId) {
		targetSet = false;
		amountSet = false;
		// Send a list of payees set for the user if any is found, otherwise, send text only
		List<Payee> payees = MockAPI.getUserPayees(userId);
		if (payees.size() > 0) {
			ButtonsTemplate buttons = new ButtonsTemplate.ButtonsTemplateBuilder(
					"Please enter the name of the payee").build();
			for (Payee payee : MockAPI.getUserPayees(userId)) {
				buttons.addAction(new MessageAction(payee.name, payee.name));
			}
			TemplateMessage message = new TemplateMessage("Please enter the name of the payee", buttons);
			Util.sendSinglePush(sender, userId, message);	
		} else {
			Util.sendSingleTextPush(sender, userId, "Please enter the name of the payee");
		}
	}
	
	/**
	 * The general event handler function that contains the logic of the billing dialogue
	 */
	@Override
	public void handleEvent(WebHookEvent event, String userId) {
		// Only text messages matter for the billing dialogue
		if (event.type() == WebHookEventType.MESSAGE) {
			MessageEvent messageEvent = (MessageEvent) event;
			if (messageEvent.message().type() == MessageType.TEXT) {
				TextMessage textMessage = (TextMessage) messageEvent.message();
				// Set the target if it is not yet set
				if (!targetSet) {
					target = textMessage.getText();
					targetSet = true;
					ButtonsTemplate buttons = 
							new ButtonsTemplate.ButtonsTemplateBuilder(
									"Please enter the amount you would like to pay")
							.addAction(new MessageAction("50", "50"))
							.addAction(new MessageAction("100", "100"))
							.addAction(new MessageAction("200", "200"))
							.addAction(new MessageAction("500", "500"))
							.build();
					Util.sendSinglePush(sender, userId, new TemplateMessage("Please enter the amount you would like to pay", buttons));
				// Set the amount if it is not yet set
				} else if (!amountSet) {
					try {
						amount = Integer.parseInt(textMessage.getText());
						amountSet = true;
					} catch (Exception e) {
						Util.sendSingleTextPush(sender, userId, "Something went wrong, cancelling transaction...");
						pop();
					}
					ConfirmTemplate confirm = new ConfirmTemplate();
					confirm.setText("CONFIRM: Transfer " + Integer.toString(amount) +
							" NT to '" + target + '\'');
					confirm.setYesAction(new MessageAction("yes", "yes"));
					confirm.setNoAction(new MessageAction("no", "no"));
					Util.sendSinglePush(sender, userId, new TemplateMessage(
							"CONFIRM: Transfer " + Integer.toString(amount) +
							" NT to '" + target + "', type yes to confirm",
							confirm));
				// Process/cancel the payment depending on whether the user confirmed or not 
				} else {
					// At this point, we are on the confirmation stage
					if (textMessage.getText().toLowerCase().equals("yes")) {
						Util.sendSingleTextPush(sender, userId, "Sending " + amount + " NT to " + target);
						MockAPI.userPayment(userId, amount, target);
					} else {
						Util.sendSingleTextPush(sender, userId, "Transaction cancelled");
					}
					pop();
				}
			}
		}
	}

}
