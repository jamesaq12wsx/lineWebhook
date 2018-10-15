package com.pershing.message;

import com.google.gson.JsonObject;
import com.pershing.quickReply.QuickReply;

/**
 * A class to represent an sticker message for the LINE Messaging API
 * - See more documentation @ https://developers.line.me/en/reference/messaging-api/#sticker-message
 * 
 * @author ianw3214
 *
 */
public class StickerMessage implements Message {

	private final String packageId;
	private final String stickerId;
	
	/**
	 * Constructor that initializes the message with package/sticker IDs
	 * 
	 * @param packageId
	 * @param stickerId
	 */
	public StickerMessage(String packageId, String stickerId) {
		this.packageId = packageId;
		this.stickerId = stickerId;
	}
	
	/**
	 * Type getter method for calling program to handle messages correctly
	 */
	public MessageType type() {
		return MessageType.STICKER;
	}

	/**
	 * Getter method for the sticker package Id
	 * @return	
	 */
	public String getPackageId() {
		return packageId;
	}
	
	/**
	 * Getter method for the sticker id
	 * @return
	 */
	public String getStickerId() {
		return stickerId;
	}
	
	/**
	 * Returns the JSON representation of the sticker message
	 * @return	the JSON representation of the message
	 */
	public JsonObject getAsJsonObject() {
		JsonObject obj = new JsonObject();
		obj.addProperty("type", "sticker");
		obj.addProperty("packageId", packageId);
		obj.addProperty("stickerId", stickerId);
		return obj;
	}

	/**
	 * Returns the JSON representation of the sticker message as a String
	 * @return	The string containing the JSON data of the message
	 */
	public String getAsJsonString() {
		return getAsJsonObject().getAsString();
	}

	@Override
	public void setQuickReply(QuickReply reply) {
		// TODO: implement this!
	}

}
