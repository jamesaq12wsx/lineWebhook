package com.pershing.event;

import com.google.gson.JsonObject;
import com.pershing.message.LocationMessage;
import com.pershing.message.Message;
import com.pershing.message.StickerMessage;
import com.pershing.message.TextMessage;

/**
 * Class representing a MessageEvent of the line Messaging API
 * 	- See more documentation @ https://developers.line.me/en/docs/messaging-api/reference/#message-event
 * 
 * @author ianw3214
 *
 */
public class MessageEvent extends WebHookEvent {

	// Token for replying to the event
	private final String replyToken;
	// Object containing the contents of the message
	// TODO: restore final modifier for message 
	private Message message;
	
	/**
	 * Main constructor which requires all properties to be set
	 * 
	 * @param timestamp
	 * @param source		
	 * @param replyToken
	 * @param message
	 */
	public MessageEvent(long timestamp, WebHookSource source, String replyToken, Message message) {
		super(WebHookEventType.MESSAGE, timestamp, source);
		this.replyToken = replyToken;
		this.message = message;
	}

	/**
	 * Constructor that directly sets message event properties by reading from an event JSON
	 * 	- Should be used to directly parse JSON received from the webhook
	 * 
	 * @param json
	 */
	public MessageEvent(JsonObject json) {
		
		// initialize common properties first
		super(WebHookEventType.MESSAGE, json);
		this.replyToken = json.get("replyToken").getAsString();
		
		// extract the message component of the event to get the message information
		JsonObject message = json.get("message").getAsJsonObject();
		String type = message.get("type").getAsString();
		
		// parse the event message differently depending on its type
		if (type.equals("text")) {
			String text = message.get("text").getAsString();
			this.message = new TextMessage(text);
		} else if (type.equals("sticker")) {
			String packageId = message.get("packageId").getAsString();
			String stickerId = message.get("stickerId").getAsString();
			this.message = new StickerMessage(packageId, stickerId);
		} else if (type.equals("location")) {
			try {
				this.message = new LocationMessage(message);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// catch all unknown cases with an error message (possibly throw an exception)
			this.message = new TextMessage("PLACEHOLDER");
		}
	}
	
	/**
	 * Getter method for the event replyToken
	 * @return	The replyToken for the message event
	 */
	public String replyToken() {
		return replyToken;
	}
	
	/**
	 * Getter method for the event message
	 * @return	The message of the event
	 */
	public Message message() {
		return message;
	}
	
}
