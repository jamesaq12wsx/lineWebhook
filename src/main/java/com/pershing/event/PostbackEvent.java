package com.pershing.event;

import com.google.gson.JsonObject;

/**
 * Class representing a PosbackEvent of the line Messaging API
 * 	- See more documentation @ https://developers.line.me/en/docs/messaging-api/reference/#postback-event
 * 
 * TODO:
 * 	- Implement the postback.params object for datetime actions
 * 
 * @author ianw3214
 *
 */
public class PostbackEvent extends WebHookEvent {

	// Token for replying to this event
	private final String replyToken;
	// Postback data
	private final String postbackData;
	// JSON object with the date and time selected by a user through a datetime picker action
	//	--- IMPLEMENT THIS ---
	
	/**
	 * Main constructor which some properties to be set
	 * 
	 * @param timestamp
	 * @param source
	 * @param replyToken
	 * @param postbackData
	 */
	public PostbackEvent(long timestamp, WebHookSource source, String replyToken, String postbackData) {
		super(WebHookEventType.POSTBACK, timestamp, source);
		this.replyToken = replyToken;
		this.postbackData = postbackData;
	}
	
	/**
	 * Constructor that directly sets postback event properties by reading from an event JSON
	 * 	- Should be used to directly parse JSON received from the webhook
	 * 
	 * @param json
	 */
	public PostbackEvent(JsonObject json) {
		super(WebHookEventType.POSTBACK, json);
		String replyToken = json.get("replyToken").getAsString();
		this.replyToken = replyToken;
		JsonObject postbackObject = json.get("postback").getAsJsonObject();
		String postbackData = postbackObject.get("data").getAsString();
		this.postbackData = postbackData;
	}
	
	/**
	 * Getter method for the event replyToken
	 * @return	The replyToken of the event
	 */
	public String replyToken() {
		return replyToken;
	}
	
	/**
	 * Getter method for the event data
	 * @return	The postbackData of the event
	 */
	public String postbackData() {
		return postbackData;
	}
	
}
