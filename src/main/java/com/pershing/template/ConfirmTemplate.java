package com.pershing.template;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.pershing.action.Action;
import com.pershing.action.MessageAction;

public class ConfirmTemplate implements Template {

	String text;
	Action yesAction;
	Action noAction;
	
	/**
	 * Type getter method for calling program to handle messages correctly
	 */
	public TemplateType type() {
		return TemplateType.CONFIRM;
	}

	// default constructor
	public ConfirmTemplate() {
		text = "";
		yesAction = new MessageAction("YES", "DEFAULT YES REPLY");
		noAction = new MessageAction("NO", "DEFAULT NO REPLY");
	}
	
	// text setter method
	public void setText(String newText) {
		text = newText;
	}
	
	// yes action setter method
	public void setYesAction(Action action) {
		yesAction = action;
	}
	
	// no action setter method
	public void setNoAction(Action action) {
		noAction = action;
	}
	
	/**
	 * Returns the JSON representation of the template message
	 * @return	the JSON representation of the message
	 */
	public JsonObject getAsJsonObject() {
		// return an empty object if any actions are missing
		if (yesAction == null || noAction == null) {
			return new JsonObject();
		}
		JsonObject obj = new JsonObject();
		obj.addProperty("type", "confirm");
		obj.addProperty("text", text);
		JsonArray actions = new JsonArray();
		actions.add(yesAction.getAsJsonObject());
		actions.add(noAction.getAsJsonObject());
		obj.add("actions", actions);
		return obj;
	}

	/**
	 * Returns the JSON representation of the template message as a String
	 * @return	The string containing the JSON data of the message
	 */
	public String getAsJsonString() {
		return getAsJsonObject().getAsString();
	}

}
