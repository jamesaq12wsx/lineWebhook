package com.pershing.action;

import com.google.gson.JsonObject;

/**
 * Action that opens a specified URI for the user 
 * 
 * NOTE that there are no direct getters for the properties since the user should never need them
 * 
 * @author ianw3214
 *
 */
public class URIAction implements Action {

	// Label for the action
	private final String label;
	// URI opened when the action is performed
	private final String URI;
	
	/**
	 * Type getter method for calling program to handle messages correctly
	 */
	public ActionType type() {
		return ActionType.URI;
	}
	
	/**
	 * Default constructor which sets all required properties of the Action 
	 * @param label
	 * @param URI
	 */
	public URIAction(String label, String URI) {
		this.label = label;
		this.URI = URI;
	}

	/**
	 * Returns the JSON representation of the message action
	 * @return	the JSON representation of the message action
	 */
	public JsonObject getAsJsonObject() {
		JsonObject result = new JsonObject();
		result.addProperty("type", "uri");
		result.addProperty("label", label);
		result.addProperty("uri", URI);
		return result;
	}

	/**
	 * Returns the JSON representation of the message action as a String
	 * @return	The string containing the JSON data of the message action
	 */
	public String getAsJsonString() {
		return getAsJsonObject().getAsString();
	}

}
