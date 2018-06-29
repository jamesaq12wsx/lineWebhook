package com.pershing.message;

import com.google.gson.JsonObject;

public class TextMessage implements Message {

	// The text contained in the text message
	private final String text;
	
	/**
	 * Type getter method for calling program to handle messages correctly
	 */
	public MessageType type() {
		return MessageType.TEXT;
	}
	
	/**
	 * Constructor that initializes the message with text
	 * @param str	The text that the message should contain
	 */
	public TextMessage(String str) {
		text = str;
	}
	
	/**
	 * Getter method to retrieve the text of the message
	 * @return	The text of the message
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * Returns the JSON representation of the text message
	 * @return	the JSON representation of the message
	 */
	public JsonObject getAsJsonObject() {
		JsonObject obj = new JsonObject();
		obj.addProperty("type", "text");
		obj.addProperty("text", text);
		return obj;
	}
	
	/**
	 * Returns the JSON representation of the text message as a String
	 * @return	The string containing the JSON data of the message
	 */
	public String getAsJsonString() {
		return getAsJsonObject().toString();
	}

}
