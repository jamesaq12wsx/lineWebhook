package com.pershing.event;

import com.google.gson.JsonObject;

/**
 * Class representing an UnfollowEvent of the line Messaging API
 * 	- See more documentation @ https://developers.line.me/en/docs/messaging-api/reference/#unfollow-event
 * 
 * 	- Note that the unfollow event has no additional properties
 * 
 * @author ianw3214
 *
 */
public class UnfollowEvent extends WebHookEvent {
	
	/**
	 * Main constructor which requires all properties to be set
	 * 
	 * @param timestamp
	 * @param source
	 */
	public UnfollowEvent(long timestamp, WebHookSource source) {
		super(WebHookEventType.UNFOLLOW, timestamp, source);
	}
	
	/**
	 * Constructor that directly sets unfollow event properties by reading from an event JSON
	 * 	- Should be used to directly parse JSON received from the webhook
	 * 
	 * @param json
	 */
	public UnfollowEvent(JsonObject json) {
		super (WebHookEventType.UNFOLLOW, json);
	}
	
}
