package com.pershing.action;

import com.google.gson.JsonObject;

/**
 * Action that sends a location sharing action to the user
 * 
 * @author ianw3214
 *
 */
public class LocationAction implements Action {

	// label for the action
	private final String label;
	
	/**
	 * Main constructor which sets the required parameters for the action
	 * 
	 * @param label		The text to be associated with the location sharing button
	 */
	public LocationAction(String label) {
		this.label = label;
	}
	
	/**
	 * Type getter method for calling program to handle messages correctly
	 */
	@Override
	public ActionType type() {
		return ActionType.LOCATION;
	}

	/**
	 * Returns the JSON representation of the location action
	 * @return	the JSON representation of the location action
	 */
	@Override
	public JsonObject getAsJsonObject() {
		JsonObject result = new JsonObject();
		result.addProperty("type", "location");
		result.addProperty("label", label);
		return result;
	}

	/**
	 * Returns the JSON representation of the location action as a String
	 * @return	The string containing the JSON data of the location picker action
	 */
	@Override
	public String getAsJsonString() {
		return getAsJsonObject().toString();
	}

}
