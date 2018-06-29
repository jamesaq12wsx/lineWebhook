package com.pershing.action;

import com.google.gson.JsonObject;

public class PostbackAction implements Action {


	String label;		// Label for the action
	String data;		// data returned via the webhook
	String displayText;	// Text displayed in the chat as a message sent by the user
	
	/**
	 * Type getter method for calling program to handle messages correctly
	 */
	public ActionType type() {
		return ActionType.POSTBACK;
	}

	/**
	 * Default constructor for the PostbackAction class
	 */
	public PostbackAction() {
		label = "";
		data = "";
		displayText = "";
	}
	
	/**
	 * Constructor with 2 input properties, since displayText is optional
	 * @param newLabel	The label of the postback action
	 * @param newData	The data to be returend by the webhook
	 */
	public PostbackAction(String newLabel, String newData) {
		label = newLabel;
		data = newData;
		displayText = "";
	}
	
	/**
	 * Constructor with all the properties of postbackAction
	 * @param newLabel			The label of the postback action
	 * @param newData			The data to be returned by the webhook
	 * @param newDisplayText	The text displayed as a message sent by the user
	 */
	public PostbackAction(String newLabel, String newData, String newDisplayText) {
		label = newLabel;
		data = newData;
		displayText = newDisplayText;
	}
	
	/**
	 * Returns the JSON representation of the message action
	 * @return	the JSON representation of the message action
	 */
	public JsonObject getAsJsonObject() {
		JsonObject obj = new JsonObject();
		obj.addProperty("type", "postback");
		obj.addProperty("label", label);
		obj.addProperty("data", data);
		if (!displayText.equals("")) obj.addProperty("displayText", displayText);
		return obj;
	}

	/**
	 * Returns the JSON representation of the message action as a String
	 * @return	The string containing the JSON data of the message action
	 */
	public String getAsJsonString() {
		return getAsJsonObject().getAsString();
	}

	
}
