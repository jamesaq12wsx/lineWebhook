package com.pershing.message;

import com.google.gson.JsonObject;
import com.pershing.quickReply.QuickReply;

/**
 * The base message interface containing general methods that messages need 
 * 
 * TODO: change message into an abstract class with base method to deal with quick replies
 * 
 * @author ianw3214
 *
 */
public interface Message {

	// A type getter function to differentiate between message types
	public MessageType type();
	// A setter function to set the quick replies for messages
	public void setQuickReply(QuickReply reply);
	// A getter function to convert message classes into a JSON object
	public JsonObject getAsJsonObject();
	// A getter function to convert message classes into a JSON object string
	public String getAsJsonString();
	
}
