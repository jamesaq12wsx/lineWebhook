package com.pershing.lineAPI;

import java.util.Base64;

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
import com.pershing.event.WebHookEvent;
import com.pershing.sender.MessageSender;
import com.pershing.sender.MessageSenderFactory;
import com.pershing.util.Util;

public class BaseWebHookHandler {

	// The channel secret of the bot
	private final String channelSecret;
	// The channel access token of the bot
	private final String channelAccessToken;
	// The sender of objects LMAO
	protected final MessageSender messageSender;
	
	// state variable of whether to log information or not
	protected boolean verbose;
	
	/**
	 * Default constructor for a base webhook handler
	 * 
	 * @param channelSecret			The channel secret of the line bot channel
	 * @param channelAccessToken	The channel access token of the line bot channel
	 */
	public BaseWebHookHandler(String channelSecret, String channelAccessToken) {
		this.channelSecret = channelSecret;
		this.channelAccessToken = channelAccessToken;
		this.messageSender = MessageSenderFactory.createDefault(this.channelAccessToken);
		verbose = false;
	}
	
	/**
	 * Constructor where MessageSender is specified and not defaulted to HTTP
	 * 
	 * @param channelSecret			The channel secret of the line bot
	 * @param channelAccessToken	The channel access token of the line bot
	 * @param sender				The messageSender object to send push/reply messages
	 */
	public BaseWebHookHandler(String channelSecret, String channelAccessToken, MessageSender sender) {
		this.channelSecret = channelSecret;
		this.channelAccessToken = channelAccessToken;
		this.messageSender = sender;
		verbose = false;
	}
	
	/**
	 * Setter method to the verbose property
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
				WebHookEvent webHookEvent = null;
				// call the corresponding handler function for the event types
				if (type.equals("message")) {
					log(">>> [WebHookHandler] Message event received");
					webHookEvent = new MessageEvent(event);
				}
				if (type.equals("follow")) {
					log(">>> [WebHookHandler] Follow event received");
					webHookEvent = new FollowEvent(event);
				}
				if (type.equals("unfollow")) {
					log(">>> [WebHookHandler] Unfollow event received");
					webHookEvent = new UnfollowEvent(event);
				}
				if (type.equals("join")) {
					log(">>> [WebHookHandler] Join event received");
					webHookEvent = new JoinEvent(event);
				}
				if (type.equals("leave")) {
					log(">>> [WebHookHandler] Leave event received");
					webHookEvent = new LeaveEvent(event);
				}
				if (type.equals("postback")) {
					log(">>> [WebHookHandler] Postback event received");
					webHookEvent = new PostbackEvent(event);
				}
				if (type.equals("beacon")) {
					log(">>> [WebHookHandler] Beacon event received");
					webHookEvent = new BeaconEvent(event);
				}
				if (type.equals("accountLink")) {
					log(">>> [WebHookHandler] Account Link event received");
					webHookEvent = new AccountLinkEvent(event);
				}
				if (webHookEvent != null) {
					String userId = webHookEvent.source().getId();
					handleEvent(userId, webHookEvent);	
				}
			}
			return true;
		} else {
			log(">>> [WebHookHandler] Header invalidated, http request no processed");
			return false;
		}
	}
	
	/**
	 * Event handler function to customize functionality of webhook handler
	 * 	- Override this function in subclasses to change functionality
	 */
	protected void handleEvent(String userId, WebHookEvent event) {
		// Just send a basic reply in the base class
		Util.sendSingleTextPush(messageSender, userId,
				"EVENT RECIEVED: " + event.type().toString());
	}
	
	/**
	 * Helper method to log information
	 * 	- Override in subclasses to change logging functionality
	 */
	protected void log(String str) {
		if (verbose) {
			System.out.println(str);	
		}
	}
	
}
