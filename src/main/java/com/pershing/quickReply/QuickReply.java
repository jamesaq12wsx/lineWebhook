package com.pershing.quickReply;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.pershing.action.Action;

/**
 * A container class of quick reply objects to set quick replies for a message
 * 	- See documentation @ https://developers.line.me/en/reference/messaging-api/#quick-reply
 * 
 * TODO: consider moving quick replies to the message package since they are closely related
 * 
 * @author ianw3214
 *
 */
public class QuickReply {

	// The list of quick reply items in the container
	private List<QuickReplyItem> items;
	
	/**
	 * Default constructor which initializes the list of quick reply items
	 */
	public QuickReply() {
		items = new ArrayList<QuickReplyItem>();
	}
	
	/**
	 * Setter method to add a quick reply item to the quick reply
	 * @param item		The quick reply item to be added
	 */
	public void addItem(QuickReplyItem item) {
		items.add(item);
	}
	
	/**
	 * Setter method to construct a new quick reply item to add via an action
	 * @param action	The action to be associated with the quick reply
	 */
	public void addItem(Action action) {
		items.add(new QuickReplyItem(action));
	}
	
	/**
	 * Returns the JSON representation of the quick reply
	 * @return	the JSON representation of the quick reply
	 */
	public final JsonObject getAsJsonObject() {
		JsonObject result = new JsonObject();
		JsonArray items = new JsonArray();
		// Add the json representations of the quick reply items to the items array
		for (QuickReplyItem item : this.items) {
			items.add(item.getAsJsonObject());
		}
		result.add("items", items);
		return result;
	}
	
	/**
	 * Returns the JSON representation of the quick reply as a String
	 * @return	The string containing the JSON data of the quick reply
	 */
	public final String getAsJsonString() {
		return getAsJsonObject().toString();
	}
	
	
}
