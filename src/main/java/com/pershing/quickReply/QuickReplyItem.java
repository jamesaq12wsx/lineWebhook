package com.pershing.quickReply;

import com.google.gson.JsonObject;
import com.pershing.action.Action;

/**
 * A base quick reply class to represent a quick reply button object
 * 	- See documentation @ https://developers.line.me/en/reference/messaging-api/#quick-reply-button-object
 * 
 * @author ianw3214
 *
 */
public class QuickReplyItem {

	// The action associated with the quick reply button object
	private final Action action;
	// The image url of the icon to be displayed in the quick reply button object
	private String imageUrl;
	
	/**
	 * Default constructor which constructs a quick reply button object using an action object
	 * @param action
	 */
	public QuickReplyItem(Action action) {
		this.action = action;
	}
	
	/**
	 * Setter method for the icon url of the quick reply button object
	 * @param url	The url to set as the icon
	 */
	public void setUrl(String url) {
		this.imageUrl = url;
	}
	
	/**
	 * Returns the JSON representation of the quick reply button object
	 * @return	the JSON representation of the quick reply button object
	 */
	public JsonObject getAsJsonObject() {
		JsonObject result = new JsonObject();
		result.addProperty("type", "action");
		if (imageUrl != null) {
			result.addProperty("imageUrl", imageUrl);
		}
		result.add("action", action.getAsJsonObject());
		return result;
	}
	
	/**
	 * Returns the JSON representation of the quick reply button object as a String
	 * @return	The string containing the JSON data of the quick reply button object 
	 */
	public String getAsJsonString() {
		return getAsJsonObject().toString();
	}
	
}
