package com.pershing.APIdemo;

import java.net.URLEncoder;
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

/**
 * A demo dialogue that querys payment information from a user and sends a QR code containing
 * 	the previously queried information
 * 
 * @author ianw3214
 *
 */
public class QRCodeDialogue extends Dialogue {

	// The rich menu to link back to the user once QR code is done
	private final String returnMenu;
	
	// The information to be encoded into the QR code
	private String target;
	private int amount;
	
	/**
	 * Constructor of the QR code dialogue which sends a prompt message to the user
	 * @param userId		The user that the QR code dialogue was pushed onto
	 * @param returnMenu	The rich menu to link back to the user once the dialogue is over
	 */
	public QRCodeDialogue(String userId, String returnMenu) {
		Util.sendSingleTextPush(sender, userId, "請輸入轉帳帳戶");
		this.returnMenu = returnMenu;
		// Unlink the user from the rich menu to open text box for typing
		sender.UnlinkRichMenu(userId);
	}
	
	/**
	 * The general event handler function that contains the logic of the QR code dialogue
	 */
	@Override
	public void handleEvent(WebHookEvent event, String userId) {
		// Only text messages should be handled by the dialogue
		if (event.type() == WebHookEventType.MESSAGE) {
			MessageEvent messageEvent = (MessageEvent) event;
			if (messageEvent.message().type() == MessageType.TEXT) {
				TextMessage textMessage = (TextMessage) messageEvent.message();
				// First set the target if it hasn't already been set
				if (target == null) {
					target = textMessage.getText();
					Util.sendSingleTextPush(sender, userId, "請輸入轉帳金額");
				// If it has been set, then send the QR code containing the queried information
				} else {
					String amountString = textMessage.getText();
					try {
						amount = Integer.parseInt(amountString);
					} catch (Exception e) {
						Util.sendSingleTextPush(sender, userId, "發生了錯誤: " + amountString);
						customPop(userId);
						return;
					}
					sendQRCodeMessage(userId);
					customPop(userId);
				}
			} else {
				// Exit the dialogue if an unexepcted type of message was recieved
				customPop(userId);
				this.root.handleEvent(event, userId);
			}
		} else {
			// Exit the dialogue if an unexepcted type of event was recieved
			customPop(userId);
			this.root.handleEvent(event, userId);
		}
	}
	
	/**
	 * Helper method to send a qr code message encoded with the information in the dialogue
	 * @param userId		The userId to send the message to
	 */
	private final void sendQRCodeMessage(String userId) {
		// Construct the image URL with the desired data in the encoding 
		String url = "https://peaceful-plains-74132.herokuapp.com/";
		url += '?';
		try {
			url += "target=" + URLEncoder.encode(target, "UTF-8");
		} catch (Exception e) { return;}
		url += '&';
		url += "amount=" + Integer.toString(amount);
		List<Message> messages = new ArrayList<Message>();
		messages.add(new TextMessage("轉帳 $" + Integer.toString(amount) + " 到 " + target));
		messages.add(new ImageMessage(url, url));
		sender.sendPush(userId, messages, "");
	}
	
	/**
	 * Custom pop function that also links back users rich menu
	 * @param userId	The userId to link a rich menu back to
	 */
	private final void customPop(String userId) {
		sender.linkRichMenu(returnMenu, userId);
		pop();
	}

}
