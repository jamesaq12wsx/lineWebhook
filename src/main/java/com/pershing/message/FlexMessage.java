package com.pershing.message;

import com.google.gson.JsonObject;
import com.pershing.quickReply.QuickReply;

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

	// Text to show when flex message can't be displayed
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
		// TODO: implement this!
		return null;
	}

	/**
	 * Returns the JSON representation of the flex message as a String
	 * @return	The string containing the JSON data of the message
	 */
	public String getAsJsonString() {
		// TODO: implement this!
		return null;
	}

	@Override
	public void setQuickReply(QuickReply reply) {
		// TODO: implement this!
	}

	
	
}
