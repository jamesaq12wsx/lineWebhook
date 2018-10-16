package com.pershing.event;

import com.google.gson.JsonObject;

/**
 * Class representing a AccountLinkEvent of the line Messaging API
 * 	- See more documentation @ https://developers.line.me/en/docs/messaging-api/reference/#account-link-event
 * 
 * @author ianw3214
 *
 */
public class AccountLinkEvent extends WebHookEvent {

	// Token for replying to this event
	private final String replyToken;
	
	/**
	 * Main constructor which requires all properties to be set
	 * 
	 * @param timestamp
	 * @param source
	 */
	public AccountLinkEvent(long timestamp, WebHookSource source, String replyToken) {
		super(WebHookEventType.ACCOUNT_LINK, timestamp, source);
		this.replyToken = replyToken;
	}
	
	/**
	 * Constructor that directly sets account link event properties by reading from an event JSON
	 * 	- Should be used to directly parse JSON received from the webhook
	 * 
	 * @param json
	 */
	public AccountLinkEvent(JsonObject json) {
		super(WebHookEventType.ACCOUNT_LINK, json);
		String replyToken = json.get("replyToken").getAsString();
		this.replyToken = replyToken;
	}
	
	/**
	 * Getter method for the event replyToken
	 * @return	The replyToken of the event
	 */
	public String replyToken() {
		return replyToken;
	}
	
	// --- IMPLEMENT THIS ---
	
}
