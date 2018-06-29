package com.pershing.message;

import com.google.gson.JsonObject;

// ---------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------
//											!!UNFINISHED!!
// ---------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------
public class FlexMessage implements Message {

	String altText;
	
	/**
	 * Type getter method for calling program to handle messages correctly
	 */
	public MessageType type() {
		return MessageType.FLEX;
	}

	/**
	 * Returns the JSON representation of the flex message
	 * @return	the JSON representation of the message
	 */
	public JsonObject getAsJsonObject() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns the JSON representation of the flex message as a String
	 * @return	The string containing the JSON data of the message
	 */
	public String getAsJsonString() {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
