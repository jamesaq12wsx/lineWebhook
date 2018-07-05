package com.pershing.lineAPI;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pershing.event.AccountLinkEvent;
import com.pershing.event.BeaconEvent;
import com.pershing.event.FollowEvent;
import com.pershing.event.JoinEvent;
import com.pershing.event.LeaveEvent;
import com.pershing.event.MessageEvent;
import com.pershing.event.PostbackEvent;
import com.pershing.event.UnfollowEvent;
import com.pershing.message.Message;
import com.pershing.message.TextMessage;
import com.pershing.sender.MessageSender;
import com.pershing.sender.MessageSenderFactory;
import com.pershing.sender.Response;

public class WebHookHandler {
	
	// The channel secret of the bot
	private final String channelSecret;
	// The channel access token of the bot
	private final String channelAccessToken;
	// The sender of objects, FEAR ME~ MWA-HA-HA
	private final MessageSender messageSender;
	
	// state variable of whether to log information or not
	protected boolean verbose;
	
	/**
	 * Default constructor for a web hook handler
	 * 
	 * @param inChannelSecret	The channel secret of the line bot
	 * @param inChannelAccessToken	The channel access token of the line bot
	 */
	public WebHookHandler(String inChannelSecret, String inChannelAccessToken) {
		channelSecret = inChannelSecret;
		channelAccessToken = inChannelAccessToken;
		verbose = false;
		messageSender = MessageSenderFactory.createDefault();
	}
	
	/**
	 * Constructor where MessageSender is specified and not defaulted to HTTP
	 * 
	 * @param inChannelSecret	The channel secret of the line bot
	 * @param inChannelAccessToken	The channel access token of the line bot
	 * @param sender			The MessageSender object to send push/reply messages
	 */
	public WebHookHandler(String inChannelSecret, String inChannelAccessToken, MessageSender sender) {
		channelSecret = inChannelSecret;
		channelAccessToken = inChannelAccessToken;
		verbose = false;
		messageSender = sender;
	}
	
	/**
	 * Setter function to the verbose property
	 * 
	 * @param verbose	
	 */
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	
	/**
	 * Helper method that validates the request header of an incoming POST request
	 * 
	 * @param header	The header (X-Line-Signature) of the request
	 * @param body		The entire body of the request (The data portion)
	 * @return
	 */
	private boolean validateHeader(String header, String body) {
		log(">>> [WebHookHandler] Validation request received, header: " + header);
		// This part is mostly copied from the line messaging API documentation
		//	See documentation @ https://developers.line.me/en/docs/messaging-api/reference/#signature-validation
		SecretKeySpec key = new SecretKeySpec(channelSecret.getBytes(), "HmacSHA256");
		String signature = "";
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(key);
			byte[] source = body.getBytes("UTF-8");
			byte[] encoded = Base64.getEncoder().encode(mac.doFinal(source));
			signature = "[" + new String(encoded) + "]";
		} catch (Exception e) {
			log (">>> [WebHookHandler] Error occured while validating the header");
			e.printStackTrace();
			// if anything goes wrong just invalidate the header
			return false;
		}
		log(">>> [WebHookHandler] RESULTING SIGNATURE: " + signature);
		return signature.equals(header);
	}
	
	/**
	 * Generic trigger function that accepts webhook requests and calls corresponding functions
	 * 
	 * @param headerSignature	The header signature of the HTTP request (used for verification)
	 * @param body				The body of the HTTP request
	 */
	public final boolean handleWebHookEvent(String headerSignature, String body) {
		log(">>> [WebHookHandler] INCOMING REQUEST");
		// first validate that the header is valid
		if (validateHeader(headerSignature, body)) {
			log(">>> [WebHookHandler] Header validated, handling http request");
			// convert the data into JSON after verifying the message
			JsonObject rawBody = new JsonParser().parse(body).getAsJsonObject();
			JsonArray events = rawBody.getAsJsonArray("events");
			for (JsonElement rawEvent : events) {
				// type cast the JsonElement to a JSON object to access its basic properties
				JsonObject event = (JsonObject) rawEvent;
				String type = event.get("type").getAsString();
				// call the corresponding handler function for the event types
				if (type.equals("message")) {
					log(">>> [WebHookHandler] Message event received");
					handleMessageEvent(new MessageEvent(event));
				}
				if (type.equals("follow")) {
					log(">>> [WebHookHandler] Follow event received");
					handleFollowEvent(new FollowEvent(event));
				}
				if (type.equals("unfollow")) {
					log(">>> [WebHookHandler] Unfollow event received");
					handleUnfollowEvent(new UnfollowEvent(event));
				}
				if (type.equals("join")) {
					log(">>> [WebHookHandler] Join event received");
					handleJoinEvent(new JoinEvent(event));
				}
				if (type.equals("leave")) {
					log(">>> [WebHookHandler] Leave event received");
					handleLeaveEvent(new LeaveEvent(event));
				}
				if (type.equals("postback")) {
					log(">>> [WebHookHandler] Postback event received");
					handlePostbackEvent(new PostbackEvent(event));
				}
				if (type.equals("beacon")) {
					log(">>> [WebHookHandler] Beacon event received");
					handleBeaconEvent(new BeaconEvent(event));
				}
				if (type.equals("accountLink")) {
					log(">>> [WebHookHandler] Account Link event received");
					handleAccountLinkEvent(new AccountLinkEvent(event));
				}
			}
			return true;
		} else {
			log(">>> [WebHookHandler] Header invalidated, http request no processed");
			return false;
		}
	}
	
	/**
	 * Handler function for incoming message events
	 * 	- See more documentation @ https://developers.line.me/en/docs/messaging-api/reference/#message-event
	 * 
	 * @param event
	 */
	protected void handleMessageEvent(MessageEvent event) {
		log(">>> [WebHookHandler] Message handler triggered, sending automated reply");
		String replyToken = event.replyToken();
		sendSingleTextReply(replyToken, "Message Event Recieved!");
	}
	
	/**
	 * Handler function for incoming follow events
	 * 	- See more documentation @ https://developers.line.me/en/docs/messaging-api/reference/#follow-event
	 * 
	 * @param event
	 */
	protected void handleFollowEvent(FollowEvent event) {
		log(">>> [WebHookHandler] Follow handler triggered, sending automated reply");
		String replyToken = event.replyToken();
		sendSingleTextReply(replyToken, "Follow Event Recieved!");
	}
	
	/**
	 * Handler function for incoming unfollow events
	 * 	- See more documentation @ https://developers.line.me/en/docs/messaging-api/reference/#unfollow-event
	 * 
	 * @param event
	 */
	protected void handleUnfollowEvent(UnfollowEvent event) {
		log(">>> [WebHookHandler] Unfollow handler triggered, sending automated reply");
		// Unfollow events have no reply tokens, so default handler does nothing
	}
	
	/**
	 * Handler function for incoming join events
	 * 	- See more documentation @ https://developers.line.me/en/docs/messaging-api/reference/#join-event
	 * 
	 * @param event
	 */
	protected void handleJoinEvent(JoinEvent event) {
		log(">>> [WebHookHandler] Join handler triggered, sending automated reply");
		String replyToken = event.replyToken();
		sendSingleTextReply(replyToken, "Join Event Recieved!");
	}

	/**
	 * Handler function for incoming leave events
	 * 	- See more documentation @ https://developers.line.me/en/docs/messaging-api/reference/#leave-event
	 * 
	 * @param event
	 */
	protected void handleLeaveEvent(LeaveEvent event) {
		log(">>> [WebHookHandler] Leave handler triggered, sending automated reply");
		// Leave events have no reply tokens, so default handler does nothing
	}

	/**
	 * Handler function for incoming postback events
	 * 	- See more documentation @ https://developers.line.me/en/docs/messaging-api/reference/#postback-event
	 * 
	 * @param event
	 */
	protected void handlePostbackEvent(PostbackEvent event) {
		log(">>> [WebHookHandler] Postback handler triggered, sending automated reply");
		String replyToken = event.replyToken();
		ArrayList<Message> reply = new ArrayList<Message>();
		reply.add(new TextMessage("Postback Event Recieved!"));
		reply.add(new TextMessage("Postback Data: " + event.postbackData()));
		sendReply(replyToken, reply);
	}
	
	/**
	 * Handler function for incoming beacon events
	 * 	- See more documentation @ https://developers.line.me/en/docs/messaging-api/reference/#beacon-event
	 * 
	 * @param event
	 */
	protected void handleBeaconEvent(BeaconEvent event) {
		log(">>> [WebHookHandler] Beacon handler triggered, sending automated reply");
		String replyToken = event.replyToken();
		sendSingleTextReply(replyToken, "Beacon Event Recieved!");
	}
	
	/**
	 * Handler function for incoming account link events
	 * 	- See more documentation @ https://developers.line.me/en/docs/messaging-api/reference/#account-link-event
	 * 
	 * @param event	The raw JSON data for account link events
	 */
	protected void handleAccountLinkEvent(AccountLinkEvent event) {
		log(">>> [WebHookHandler] Account Link handler triggered, sending automated reply");
		String replyToken = event.replyToken();
		sendSingleTextReply(replyToken, "Account Link Event Recieved!");
	}
	
	/**
	 * Function to send a reply to an event
	 * 	- only sends text message for now
	 * 
	 * @param token			The reply token to respond to
	 * @param replyMessages The messages to be sent
	 * @return				The response from sending the reply
	 */
	final protected Response sendReply(String token, List<Message> replyMessages) {
		
		return messageSender.sendReply(channelAccessToken, token, replyMessages);
	}

	/**
	 * Function to send a push to a user
	 * 	- Only sends text messages for now
	 * 
	 * @param userId		The userId of the receiver
	 * @param pushMessages	The messages to be sent
	 * @return				The response from sending the push notification
	 */
	final protected Response sendPush(String userId, List<Message> pushMessages) {
		
		return messageSender.sendPush(channelAccessToken, userId, pushMessages);
		
	}
	
	/**
	 * Function to send a reply with a single text message to an event
	 * @param token	The reply token to respond to
	 * @param text	The text message to be sent
	 */
	final protected void sendSingleTextReply(String token, String text) {
		ArrayList<Message> reply = new ArrayList<Message>();
		reply.add(new TextMessage(text));
		sendReply(token, reply);
	}
	
	/**
	 * Function to send a push notification with a single text message
	 * @param userId	The userId of the receiver
	 * @param text		The messages to be sent
	 */
	final protected void sendSingleTextPush(String userId, String text) {
		ArrayList<Message> message = new ArrayList<Message>();
		message.add(new TextMessage(text));
		sendPush(userId, message);
	}

	/**
	 * Helper method to log information
	 * 	- Override in subclasses to change logging functionality
	 * 
	 * @param str	Text to be written to the log
	 */
	protected void log(String str) {
		if (verbose) {
			System.out.println(str);	
		}
	}
	
}
