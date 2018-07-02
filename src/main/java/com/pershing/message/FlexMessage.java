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
/**
 * Flex messages represent messages with a customizable layout
 * 	- See documentation @ https://developers.line.me/en/docs/messaging-api/reference/#flex-message
 * 
 * NOTE that flex messages are only used for sending, not receiving
 * 
 * @author ianw3214
 *
 */
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
