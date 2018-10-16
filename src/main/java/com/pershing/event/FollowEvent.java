package com.pershing.event;

import com.google.gson.JsonObject;

/**
 * Class representing a FollowEvent of the line Messaging API
 * 	- See more documentation @ https://developers.line.me/en/docs/messaging-api/reference/#follow-event
 * 
 * @author ianw3214
 *
 */
public class FollowEvent extends WebHookEvent {

	// Token for replying to this event
	private final String replyToken;
	
	/**
	 * Main constructor which requires all properties to be set
	 * 
	 * @param timestamp
	 * @param source
	 * @param replyToken
	 */
	public FollowEvent(long timestamp, WebHookSource source, String replyToken) {
		super(WebHookEventType.FOLLOW, timestamp, source);
		this.replyToken = replyToken;
	}
	
	
	/**
	 * Constructor that directly sets follow event properties by reading from an event JSON
	 * 	- Should be used to directly parse JSON received from the webhook
	 * 
	 * @param json
	 */
	public FollowEvent(JsonObject json) {
		super(WebHookEventType.FOLLOW, json);
		this.replyToken = json.get("replyToken").getAsString();
	}
	
	/**
	 * Getter method for the event replyToken
	 * @return	The replyToken of the event
	 */
	public String replyToken() {
		return replyToken;
	}
	
}
