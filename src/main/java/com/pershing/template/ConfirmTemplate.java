package com.pershing.template;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.pershing.action.Action;
import com.pershing.action.MessageAction;

/**
 * A confirm template is a tempalte with two buttons
 * 	- See documentation @ https://developers.line.me/en/reference/messaging-api/#confirm
 * 
 * @author ianw3214
 *
 */
public class ConfirmTemplate implements Template {

	// The text to be sent with the template message
	String text;
	// The action set for the left button of the template
	Action yesAction;
	// The action set for the right button of the template
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
	
	// Yes action setter method (left action)
	public void setYesAction(Action action) {
		yesAction = action;
	}
	
	// No action setter method (right action)
	public void setNoAction(Action action) {
		noAction = action;
	}
	
	/**
	 * Returns the JSON representation of the confirm template
	 * @return	the JSON representation of the confirm template
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
	 * Returns the JSON representation of the confirm template as a String
	 * @return	The string containing the JSON data of the confirm template
	 */
	public String getAsJsonString() {
		return getAsJsonObject().getAsString();
	}

}
