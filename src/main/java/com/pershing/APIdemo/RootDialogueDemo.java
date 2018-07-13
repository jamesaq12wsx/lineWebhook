package com.pershing.APIdemo;

import java.util.ArrayList;
import java.util.List;

import com.pershing.action.MessageAction;
import com.pershing.action.PostbackAction;
import com.pershing.dialogue.RootDialogue;
import com.pershing.event.FollowEvent;
import com.pershing.event.MessageEvent;
import com.pershing.event.PostbackEvent;
import com.pershing.event.WebHookEvent;
import com.pershing.event.WebHookEventType;
import com.pershing.message.Message;
import com.pershing.message.MessageType;
import com.pershing.message.TemplateMessage;
import com.pershing.message.TextMessage;
import com.pershing.mockAPI.Account;
import com.pershing.mockAPI.MockAPI;
import com.pershing.template.ButtonsTemplate;
import com.pershing.util.Util;

public class RootDialogueDemo extends RootDialogue {

	private static final String richMenuId = "richmenu-052dac80d35611ab4c4984c78891e437";
	
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
		if (event.type() == WebHookEventType.POSTBACK) {
			PostbackEvent postbackEvent = (PostbackEvent) event;
			handlePostbackEvent(postbackEvent, userId);
		}
		if (event.type() == WebHookEventType.UNFOLLOW) {
			// unlink the rich menu if the user unfollows
			sender.UnlinkRichMenu(userId);
		}
	}
	
	private void parseTextMessage(String text, String userId) {
		if (text.toLowerCase().contains("help")) {
			Util.sendSingleTextPush(sender, userId, "Type accounts to check your account details");			
		} else if (text.toLowerCase().contains("setup")) {
			push(new SetupDialogue(userId));
		} else if (text.toLowerCase().contains("accounts")) {
			handleAccountCommand(userId);
		} else if (text.toLowerCase().contains("billing")) {
			push(new BillingDialogue(userId));
		} else {
			Util.sendSingleTextPush(sender, userId, "Message not understood, type help for a list of commands");
		}
	}
	
	private void handleAccountCommand(String userId) {
		// Make sure the account is verified first
		if (!MockAPI.userValid(userId)) {
			Util.sendSingleTextPush(sender, userId, "Account not yet validated, type setup to get started.");
			return;
		}
		// Get the accounts overview and construct a menu to view account details
		List<Account> accounts = MockAPI.getUserAccounts(userId);
		String reply = "ACCOUNTS:\n";
		ButtonsTemplate buttons = new ButtonsTemplate.ButtonsTemplateBuilder("Select an account to view details")
				.build();
		for (Account account : accounts) {
			reply += account.name + ": " + Integer.toString(account.balance) + '\n';
			buttons.addAction(new PostbackAction(account.name, "ACCOUNT" + account.id));
		}
		TextMessage overView = new TextMessage(reply);
		TemplateMessage menu = new TemplateMessage("Menu to select account details", buttons);
		List<Message> messages = new ArrayList<Message>();
		messages.add(overView);
		messages.add(menu);
		sender.sendPush(userId, messages, "");
	}
	
	private void handlePostbackEvent(PostbackEvent event, String userId) {
		String data = event.postbackData();
		if (data.substring(0, 7).equals("ACCOUNT")) {
			// Make sure the account is verified first
			if (!MockAPI.userValid(userId)) {
				Util.sendSingleTextReply(sender, event.replyToken(), "Account not yet validated, type setup to get started.");
				return;
			}
			String id = data.substring(7);
			System.out.println("LOOKING FOR: " + id);
			// try to get the user account info
			List<Account> accounts = MockAPI.getUserAccounts(userId);
			for (Account account : accounts) {
				System.out.println("ACCOUNT ID: " + account.id);
				if (account.id.equals(id)) {
					String overview  = account.name + '\n';
					overview += "ID: " + account.id + '\n';
					overview += "BALANCE: " + account.balance;
					String history = "LAST 10 TRANSACTIONS: \n";
					for (String transaction : account.history) {
						history += transaction + '\n';
					}
					List<Message> reply = new ArrayList<Message>();
					reply.add(new TextMessage(overview));
					reply.add(new TextMessage(history));
					sender.sendReply(event.replyToken(), reply, "");
					return;
				}
			}
			// if this code runs, then the account was not found
			Util.sendSingleTextPush(sender, userId, "Sorry, something went wrong while looking for the account.");
		}
	}

}
