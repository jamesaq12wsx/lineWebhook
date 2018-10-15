package com.pershing.event;

import com.google.gson.JsonObject;

/**
 * Class representing a JoinEvent of the line Messaging API
 * 	- See more documentation @ https://developers.line.me/en/docs/messaging-api/reference/#join-event
 * 
 * @author ianw3214
 *
 */
public class JoinEvent extends WebHookEvent {

	// Token for replying to this event
	private final String replyToken;
	
	/**
	 * Main constructor which requires all the properties to be set
	 * 
	 * @param timestamp
	 * @param source
	 * @param replyToken
	 */
	public JoinEvent(long timestamp, WebHookSource source, String replyToken) {
		super(WebHookEventType.JOIN, timestamp, source);
		this.replyToken = replyToken;
	}
	
	/**
	 * Constructor that directly sets join event properties by reading from an event JSON
	 * 	- Should be used to directly parse JSON received from the webhook
	 * 
	 * @param json
	 */
	public JoinEvent(JsonObject json) {
		super(WebHookEventType.JOIN, json);
		String replyToken = json.get("replyToken").getAsString();
		this.replyToken = replyToken;
	}
	
	/**
	 * Getter method for the reply token
	 * @return	The replyToken of the event
	 */
	public String replyToken() {
		return replyToken;
	}
	
}
