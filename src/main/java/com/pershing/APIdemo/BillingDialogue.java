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

public class BillingDialogue extends Dialogue {

	private boolean targetSet;
	private String target;
	private boolean amountSet;
	private int amount;
	
	public BillingDialogue(String userId) {
		targetSet = false;
		amountSet = false;
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
	
	@Override
	public void handleEvent(WebHookEvent event, String userId) {
		if (event.type() == WebHookEventType.MESSAGE) {
			MessageEvent messageEvent = (MessageEvent) event;
			if (messageEvent.message().type() == MessageType.TEXT) {
				TextMessage textMessage = (TextMessage) messageEvent.message();
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
