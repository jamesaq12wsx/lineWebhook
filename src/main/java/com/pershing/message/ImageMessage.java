package com.pershing.message;

import com.google.gson.JsonObject;
import com.pershing.quickReply.QuickReply;

/**
 * A class to represent an image message for the LINE Messaging API
 * 	- See documentation @ https://developers.line.me/en/reference/messaging-api/#image-message
 * 
 * NOTE that image this implementation of the image message is used for sending images only, the
 * 		data for receiving an image is different to sending
 * 	- See documentation @ https://developers.line.me/en/reference/messaging-api/#wh-image
 * 
 * @author ianw3214
 *
 */
public class ImageMessage implements Message {

	// The url of the image
	private final String originalContentUrl;
	// The url for the preview of the image
	private final String previewImageUrl;
	
	/**
	 * Constructor which sets the corresponding URLs of the message
	 * @param originalContentUrl	The url of the image
	 * @param previewImageUrl		The url for the preview of the image
	 */
	public ImageMessage(String originalContentUrl, String previewImageUrl) {
		this.originalContentUrl = originalContentUrl == null ? "" : originalContentUrl;
		this.previewImageUrl = previewImageUrl == null ? "" : previewImageUrl;
	}
	
	/**
	 * Type getter method for calling program to handle messages correctly
	 */
	@Override
	public MessageType type() {
		return MessageType.IMAGE;
	}

	/**
	 * Returns the JSON representation of the image message
	 * @return	the JSON representation of the image message
	 */
	@Override
	public JsonObject getAsJsonObject() {
		JsonObject result = new JsonObject();
		result.addProperty("type", "image");
		result.addProperty("originalContentUrl", originalContentUrl);
		result.addProperty("previewImageUrl", previewImageUrl);
		return result;
	}

	/**
	 * Returns the JSON representation of the image message as a String
	 * @return	The string containing the JSON data of the image message
	 */
	@Override
	public String getAsJsonString() {
		return getAsJsonObject().toString();
	}

	@Override
	public void setQuickReply(QuickReply reply) {
		// TODO: implement this!
	}

}
