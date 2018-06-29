package com.pershing.action;

import com.google.gson.JsonObject;

public class MessageAction implements Action {

	String label;	// Label for the action
	String text;	// Text sent when the action is performed
	
	/**
	 * Type getter method for calling program to handle messages correctly
	 */
	public ActionType type() {
		return ActionType.MESSAGE;
	}

	/**
	 * Default constructor for the MessageAction class
	 */
	public MessageAction() {
		label = "";
		text = "";
	}
	
	/**
	 * Constructor with the class properties
	 * @param lab	The label of the message
	 * @param tex	The text of th message
	 */
	public MessageAction(String lab, String tex) {
		label = lab;
		text = tex;
	}
	
	/**
	 * Returns the JSON representation of the message action
	 * @return	the JSON representation of the message action
	 */
	public JsonObject getAsJsonObject() {
		JsonObject obj = new JsonObject();
		obj.addProperty("type", "message");
		obj.addProperty("label", label);
		obj.addProperty("text", text);
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
