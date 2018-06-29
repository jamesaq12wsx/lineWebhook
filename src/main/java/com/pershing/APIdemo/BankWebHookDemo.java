package com.pershing.APIdemo;

import java.util.ArrayList;
import java.util.List;

import com.pershing.action.Action;
import com.pershing.action.MessageAction;
import com.pershing.action.PostbackAction;
import com.pershing.event.FollowEvent;
import com.pershing.event.MessageEvent;
import com.pershing.event.PostbackEvent;
import com.pershing.lineAPI.WebHookHandler;
import com.pershing.message.Message;
import com.pershing.message.MessageType;
import com.pershing.message.TemplateMessage;
import com.pershing.message.TextMessage;
import com.pershing.template.ButtonsTemplate;
import com.pershing.template.ConfirmTemplate;

public class BankWebHookDemo extends WebHookHandler {

	List<User> users;
	
	/**
	 * Default constructor for bank demo
	 * 
	 * @param inChannelSecret	The channel secret of the line bot
	 * @param inChannelAccessToken	The channel access token of the line bot
	 */ 
	public BankWebHookDemo(String inChannelSecret, String inChannelAccessToken) {
		super(inChannelSecret, inChannelAccessToken);
		users = new ArrayList<User>();
	}
	
	public void sendPushToUsers(String text) {
		for (User u : users) {
			sendSingleTextPush(u.lineId, text);
		}
	}

	/**
	 * Handler function for incoming message events
	 * 
	 * @param event
	 */
	@Override
	protected void handleMessageEvent(MessageEvent event) {
		String replyToken = event.replyToken();
		// find the userId
		String lineId = event.source().getId();
		// parse the message and handle appropriately
		Message message = event.message();
		if (message.type() == MessageType.TEXT) {
			TextMessage textMessage = (TextMessage)message;
			log(">>> [DEMO] Handling incoming text message");
			String text = textMessage.getText();
			parseTextMessage(lineId, replyToken, text);
		}
	}
	
	/**
	 * Handler function for incoming follow events
	 * 
	 * @param event
	 */
	@Override
	protected void handleFollowEvent(FollowEvent event) {
		String replyToken = event.replyToken();
		// add the user to the database
		String lineId = event.source().getId();
		users.add(new User(lineId, 100));
		if (verbose) System.out.println(">>> [DEMO] Sending reply to follow event");
		// send a prompt for the user to setup their account
		List<Action> actions = new ArrayList<Action>();
		actions.add(new PostbackAction("setup", "setup"));
		// create the actual template and construct the message
		ButtonsTemplate buttons = new ButtonsTemplate.
				ButtonsTemplateBuilder("Thanks for using bank app. Press the setup button or type setup to get started.")
				.actions(actions)
				.createButtonsTemplate();
		ArrayList<Message> reply = new ArrayList<Message>();
		reply.add(new TemplateMessage("Type setup to get started.", buttons));
		// send the reply
		sendReply(replyToken, reply);
	}
	
	/**
	 * Handler function for incoming postback events
	 * 
	 * @param event
	 */
	@Override
	protected void handlePostbackEvent(PostbackEvent event) {
		// get the event user and reply token
		String lineId = event.source().getId();
		String replyToken = event.replyToken();
		// handle the event based on the kind of postback
		String data = event.postbackData();
		log(">>> [DEMO] POSTBACK DATA: " + data);
		// setup the user
		if (data.equals("setup")) {
			startSetup(lineId);
		}
		if (data.equals("balance")) {
			sendUserBalance(findUserViaLine(lineId));
		}
		if (data.equals("transfer")) {
			startTransfer(lineId);
		}
		// if we are expecting a confirm, then we process YES and NO postbacks
		User u = findUserViaLine(lineId);
		if (u.currentExpectedInput == ExpectedInput.TRANSFER_CONFIRM) {
			if (data.equals("YES")) {
				User transferSource = findUserViaLine(lineId);
				if (transferSource == null) {
					sendSingleTextPush(lineId, "Transfer cancelled: source user invalid");
				}
				User transferTarget = findUser(transferSource.currentTransferTArget);
				if (transferTarget == null) {
					sendSingleTextPush(lineId, "Transfer cancelled: target user invalid");
				}
				// after validating both users exist, send the transfer
				transferSource.balance -= transferSource.transferAmount;
				transferTarget.balance += transferSource.transferAmount;
				List<Message> reply = new ArrayList<Message>();
				reply.add(new TextMessage("Transferred: " + transferSource.transferAmount + " dollars to: " + transferSource.currentTransferTArget));
				reply.add(new TextMessage("New balance: " + transferSource.balance));
				sendReply(replyToken, reply);
				// also send a push notification to the recipient of the transfer
				String message = "Transfer received from " + transferSource.userId + ", Amount: " + transferSource.transferAmount;
				sendSingleTextPush(transferTarget.lineId, message);
			}
			if (data.equals("NO")) {
				sendSingleTextReply(replyToken, "Transfer cancelled");
			}
			u.currentExpectedInput = ExpectedInput.NONE;
		}
	}

	private void parseTextMessage(String lineId, String replyToken, String text) {
		System.out.println(">>> [DEMO] Parsing text: " + text);
		// see if we are expecting any input from the user first
		User user = findUserViaLine(lineId);
		if (user != null) {
			if (user.currentExpectedInput != ExpectedInput.NONE) {
				handleExpectedInput(user, text, replyToken);	
			} else {
				text = text.toLowerCase();
				if (utils.containsString(text, "setup")) {
					startSetup(lineId);
				} else if (utils.containsString(text, "balance")) {
					// send the user his/her balance
					sendUserBalance(user);
				} else if (utils.containsString(text, "menu")) {
					// create the button actions
					List<Action> actions = new ArrayList<Action>();
					actions.add(new PostbackAction("Check Balance", "balance"));
					actions.add(new MessageAction("Transfer Money", "transfer"));
					// create the actual template and construct the message
					ButtonsTemplate buttons = new ButtonsTemplate.ButtonsTemplateBuilder("MENU").actions(actions).createButtonsTemplate();
					ArrayList<Message> reply = new ArrayList<Message>();
					reply.add(new TemplateMessage("MENU TEMPLATE", buttons));
					// send the reply
					sendReply(replyToken, reply);
				} else if (utils.containsString(text, "transfer")) {
					startTransfer(lineId);
				} else if (utils.containsString(text, "info")) {
					sendUserInfo(user);
				} else if (utils.containsString(text,  "help")) {
					String reply = "SETUP: setup account w/ bank or something\n";
					reply += "BALANCE: show current account balance\n";
					reply += "MENU: show menu of available actions\n";
					reply += "TRANSFER: transfer money to target user";
					sendSingleTextReply(replyToken, reply);
				} else {
					// not a valid command
					sendSingleTextReply(replyToken, "Sorry, your message couldn't be understood");
				}
			}
		} else {
			if (utils.containsString(text, "setup")) {
				startSetup(lineId);
			} else {
				sendSingleTextReply(replyToken, "Sorry, user not registered in database. Type setup to set up");
			}
		}
	}
	
	/**
	 * Finds the User object associated with a given ID
	 * 
	 * @param userId
	 * @return
	 */
	private User findUser(String userId) {
		for (User u : users) {
			if (u.userId.equals(userId)) {
				return u;
			}
		}
		return null;
	}
	
	/**
	 * Finds the User object associated with a given line ID
	 * 
	 * @param lineId
	 * @return
	 */
	private User findUserViaLine(String lineId) {
		for (User u : users) {
			if (u.lineId.equals(lineId)) {
				return u;
			}
		}
		return null;
	}
	
	/**
	 * Starts the setup process for a certain user
	 * @param lineId
	 */
	private void startSetup(String lineId) {
		sendSingleTextPush(lineId, "Initiating setup");
		sendSingleTextPush(lineId, "Please start by entering your bank ID");
		findUserViaLine(lineId).currentExpectedInput = ExpectedInput.USER_ID;
	}
	
	private void startTransfer(String lineId) {
		sendSingleTextPush(lineId, "Please enter the bank ID of the account to transfer to");
		findUserViaLine(lineId).currentExpectedInput = ExpectedInput.TRANSFER_TARGET_ID;
	}
	
	/**
	 * Handler function for when a user is expecting input
	 * @param user
	 * @param text
	 */
	private void handleExpectedInput(User user, String text, String replyToken) {
		System.out.println(">>> [DEMO] HANDLING EXPECTED INPUT!!!");
		switch(user.currentExpectedInput) {
		case NONE: {
			System.out.println(">>> [DEMO] NOT ACTUALLY EXPECTING ANY INPUT");
			// do nothing for now
		} break;
		case USER_ID: {
			System.out.println(">>> [DEMO] PARSING MESSAGE AS USER_ID");
			user.userId = text;
			user.currentExpectedInput = ExpectedInput.NONE;
			List<Message> reply = new ArrayList<Message>();
			reply.add(new TextMessage("ID registered: " + text));
			reply.add(new TextMessage("Type help for a list of commands"));
			sendReply(replyToken, reply);
		} break;
		case TRANSFER_TARGET_ID: {
			System.out.println(">>> [DEMO] PARSING MESSAGE AS TRANSFER_TARGET_ID");
			// try to find the target user in the system
			User target = findUser(text);
			if (target == null) {
				sendSingleTextReply(replyToken, "Sorry, target user not found.");
				user.currentExpectedInput = ExpectedInput.NONE;
			} else {
				user.currentTransferTArget = text;
				sendSingleTextReply(replyToken, "Enter the amount you would like to transfer");
				user.currentExpectedInput = ExpectedInput.TRANSFER_AMOUNT;
			}
		} break;
		case TRANSFER_AMOUNT: {
			System.out.println(">>> [DEMO] PARSING MESSAGE AS TRANSFER_AMOUNT");
			// try to parse the number as a string
			int amount = Integer.parseInt(text);
			user.transferAmount = amount;
			// create the confirm template
			ConfirmTemplate confirm = new ConfirmTemplate();
			confirm.setYesAction(new PostbackAction("Yes", "YES"));
			confirm.setNoAction(new PostbackAction("No", "NO"));
			confirm.setText("Confirm transfer to: " + user.currentTransferTArget + " for " + amount + " dollars?");
			ArrayList<Message> reply = new ArrayList<Message>();
			reply.add(new TemplateMessage("CONFIRM TEMPLATE", confirm));
			user.currentExpectedInput = ExpectedInput.TRANSFER_CONFIRM;
			// send the reply
			sendReply(replyToken, reply);
		}
		default: {
			// do nothing for now
			System.out.println(">>> [DEMO] NO EXPECTED INPUT");
		} break;
		}
	}
	
	/**
	 * Send a user his/her balance
	 * @param user
	 */
	private void sendUserBalance(User user) {
		if (user != null) {
			if (verbose) System.out.println(">>> Sending user balance: " + user.balance + " to user: " + user.userId);
			String message = "Current Balance: " + Integer.toString(user.balance);
			sendSingleTextPush(user.lineId, message);
		}
	}
	
	/**
	 * Send a user his/her account info
	 * @param user
	 */
	private void sendUserInfo(User user) {
		if (user != null) {
			if (verbose) System.out.println(">>> Sending user info to user: " + user.userId);
			String message = "User ID: " + user.userId;
			sendSingleTextPush(user.lineId, message);
		}
	}
	
	
}
