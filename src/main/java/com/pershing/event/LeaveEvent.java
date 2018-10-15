package com.pershing.event;

import com.google.gson.JsonObject;

/**
 * Class representing a LeaveEvent of the line Messaging API
 * 	- See more documentation @ https://developers.line.me/en/docs/messaging-api/reference/#leave-event
 *
 * 	- Note that the leave event doesn't have any additional data
 * 
 * @author ianw3214
 *
 */
public class LeaveEvent extends WebHookEvent {

	/**
	 * Main constructor which requires all properties to be set
	 * 
	 * @param timestamp
	 * @param source
	 */
	public LeaveEvent(long timestamp, WebHookSource source) {
		super(WebHookEventType.LEAVE, timestamp, source);
	}
	
	/**
	 * Constructor that directly sets leave event properties by reading from an event JSON
	 * 	- Should be used to directly parse JSON received from the webhook
	 * 
	 * @param json
	 */
	public LeaveEvent(JsonObject json) {
		super(WebHookEventType.LEAVE, json);
	}
	
}
