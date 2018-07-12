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
import com.pershing.template.ButtonsTemplate;
import com.pershing.template.ConfirmTemplate;
import com.pershing.util.Util;

public class BillPaymentDialogue extends Dialogue {

	private String payee;
	private Integer amount;
	
	public BillPaymentDialogue(String userId) {
		List<String> payees = mockRemoteAPI.getUserPayees(userId);
		if (payees.size() > 0) {
			ButtonsTemplate buttons = new ButtonsTemplate.ButtonsTemplateBuilder(
					"Please choose a payee from the menu below or message the name of the payee.").build();	
			for (String payee : payees) {
				buttons.addAction(new MessageAction(payee, payee));
			}
			TemplateMessage message = new TemplateMessage(
					"Please choose a payee from the menu below or message the name of the payee.",
					buttons);
			Util.sendSinglePush(sender, userId, message);
			Util.sendSingleTextPush(sender, userId, message.getAsJsonString());
			System.out.println(" +============ FLAG ================+");
			System.out.println(message.getAsJsonString());
			System.out.println(" +============ FLAG ================+");
		} else {
			Util.sendSingleTextPush(sender, userId, "Please enter the name of the payee.");
		}
	}
	
	@Override
	public void handleEvent(WebHookEvent event, String userId) {
		if (event.type() == WebHookEventType.MESSAGE) {
			MessageEvent messageEvent = (MessageEvent) event;
			if (messageEvent.message().type() == MessageType.TEXT) {
				TextMessage textMessage = (TextMessage) messageEvent.message();
				if (payee == null) {
					payee = textMessage.getText();
					ButtonsTemplate buttons = new ButtonsTemplate.ButtonsTemplateBuilder(
							"Choose or type the amount you would like to pay.")
							.addAction(new MessageAction("50", "50"))
							.addAction(new MessageAction("100", "100"))
							.addAction(new MessageAction("200", "200"))
							.addAction(new MessageAction("500", "500"))
							.build();
					TemplateMessage message = new TemplateMessage(
							"Choose or type the amount you would like to pay.",
							buttons);
					Util.sendSinglePush(sender, userId, message);
				} else if (amount == null) {
					String amountStr = textMessage.getText();
					try {
						amount = Integer.parseInt(amountStr);
						ConfirmTemplate confirm = new ConfirmTemplate();
						confirm.setYesAction(new MessageAction("yes", "yes"));
						confirm.setNoAction(new MessageAction("no", "no"));
						confirm.setText("CONFIRM: Send " + amount.toString() + " dollars to '" + payee + "'");
						TemplateMessage message = new TemplateMessage("Type yes to confirm the transaction", confirm);
						Util.sendSinglePush(sender, userId, message);
					} catch (Exception e) {
						Util.sendSingleTextPush(sender, userId, 
								textMessage + " could not be read as a number, please try again.");
					}
				} else {
					if (textMessage.getText().toLowerCase().equals("yes")) {
						mockRemoteAPI.TakeUserMoney(userId, amount);
						Util.sendSingleTextPush(sender, userId, "Transaction successful, transferring " + 
								amount.toString() + " dollars to " + payee);
						// Only add the payee to the users list if the transaction went through
						mockRemoteAPI.addUserPayee(userId, payee);
						pop();
					} else {
						Util.sendSingleTextPush(sender, userId, "Transaction cancelled.");
						pop();
					}
				}
			}
		}
	}

}
