package com.pershing.event;

import com.google.gson.JsonObject;

/**
 * Class representing incoming events from the webhook
 * 
 * @author ianw3214
 *
 */
public abstract class WebHookEvent {

	// Identifier for the type of event
	protected final WebHookEventType type;
	// Time of the event in milliseconds
	protected final long timestamp;
	// Source object of the event
	protected final WebHookSource source;
	
	/**
	 * Main constructor that requires all 3 properties to be set
	 * 
	 * @param type
	 * @param timestamp
	 * @param source
	 */
	protected WebHookEvent(WebHookEventType type, long timestamp, WebHookSource source) {
		this.type = type;
		this.timestamp = timestamp;
		this.source = source;
	}
	
	/**
	 * Constructor to set event parameters based on JSON object
	 * 
	 * @param type
	 * @param json
	 */
	protected WebHookEvent(WebHookEventType type, JsonObject json) {
		this.type = type;
		long timestamp = json.get("timestamp").getAsLong();
		this.timestamp = timestamp;
		JsonObject sourceJson = json.get("source").getAsJsonObject();
		WebHookSource source = WebHookSource.buildFromJson(sourceJson);
		this.source = source;
	}
	
	/**
	 * Getter method for the event type
	 * @return	The type of the current event object
	 */
	public WebHookEventType type() {
		return type;
	}
	
	/**
	 * Getter method for the event timestamp
	 * @return	The timestamp of the current event object
	 */
	public long timestamp() {
		return timestamp;
	}
	
	/**
	 * Getter method for the eent source
	 * @return	The source of the current event object
	 */
	public WebHookSource source() {
		return source;
	}
	
}
