package com.pershing.action;

import com.google.gson.JsonObject;

/**
 * Action that sends a datetime picker message to the user
 * 
 * TODO:
 * 	- Use an enum for the mode instead of a string
 *  - Possible custom implementation of date/time object instead of strings
 * 
 * @author ianw3214
 *
 */
public class DatetimePickerAction implements Action {

	// label for the action
	private final String label;
	// String returned via webhook in the postback data property
	private final String data;
	// Action mode (date, time, datetime)
	private final String mode;
	// initial value of date or time
	private String initial;
	// largest date or time value that can be selected
	private String max;
	// smallest date or time value that can be selected
	private String min;
	
	/**
	 * Type getter method for calling program to handle messages correctly
	 */
	public ActionType type() {
		return ActionType.DATETIME;
	}
	
	/**
	 * Main constructor which sets the required parameters for the action
	 * 
	 * @param label	The label for the action
	 * @param data	The data returned via webhook
	 * @param mode	The action mode (date, time, datetime)
	 */
	public DatetimePickerAction(String label, String data, String mode) {
		this.label = label;
		this.data = data;
		this.mode = mode;
	}
	
	// SETTER METHODS
	public void setInitial(String initial) {
		this.initial = initial;
	}
	public void setMax(String max) {
		this.max = max;
	}
	public void setMin(String min) {
		this.min = min;
	}

	/**
	 * Returns the JSON representation of the datetime picker action
	 * @return	the JSON representation of the datetime picker action
	 */
	public JsonObject getAsJsonObject() {
		JsonObject result = new JsonObject();
		result.addProperty("type", "datetimepicker");
		result.addProperty("label", label);
		result.addProperty("data", data);
		result.addProperty("mode", mode);
		if (initial != null) result.addProperty("initial", initial);
		if (max != null) result.addProperty("max", max);
		if (min != null) result.addProperty("min", min);
		return result;
	}

	/**
	 * Returns the JSON representation of the datetime picker action as a String
	 * @return	The string containing the JSON data of the datetime picker action
	 */
	public String getAsJsonString() {
		return getAsJsonObject().getAsString();
	}

}
