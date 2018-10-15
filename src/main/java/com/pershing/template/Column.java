package com.pershing.template;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.pershing.action.Action;

/**
 * A column object representing a column of a carousel
 *	- See documentation @ https://developers.line.me/en/reference/messaging-api/#column-object-for-carousel 
 * 
 * @author ianw3214
 *
 */
public class Column {

	// The image Url of the column
	private String thumbnailImageUrl;
	// The background colour of the column
	private String imageBackgroundColour;
	// The title of the column
	private String title;
	// The message text of the column, REQUIRED
	private final String text;
	// The default action that executes when the image is tapped
	private Action defaultAction;
	// The list of actions to list as buttons in the column, max 3
	private List<Action> actions;
	
	/**
	 * Basic constructor which sets the required text field of the column
	 * @param text		The message text of the column
	 */
	public Column(String text) {
		this.text = text;
	}
	
	// Setter method for thumbnail image url
	public void setThumbnailImageUrl(String thumbnailImageUrl) {
		this.thumbnailImageUrl = thumbnailImageUrl;
	}
	
	// Setter method for image background colour
	public void setImageBackgroundcolour(String imageBackgroundColour) {
		this.imageBackgroundColour = imageBackgroundColour;
	}
	
	// Setter method for column title
	public void setTitle(String title) {
		this.title = title;
	}
	
	// Setter method for action that executes when the image is tapped
	public void setDefaultAction(Action action) {
		this.defaultAction = action;
	}
	
	/**
	 * Add an action to the current list of actions
	 * @param action		The new action to be added
	 */
	public void addAction(Action action) {
		if (this.actions == null) this.actions = new ArrayList<Action>();
		this.actions.add(action);
	}
	
	// Getter method for the current number of actions in the column
	public int numActions() {
		return this.actions.size();
	}
	
	/**
	 * Returns the JSON representation of the column object
	 * @return	the JSON representation of the column object
	 */
	public JsonObject getAsJsonObject() {
		JsonObject result = new JsonObject();
		// Only add the properties that are set
		if (thumbnailImageUrl != null) result.addProperty("thumbnailImageUrl", thumbnailImageUrl);
		if (imageBackgroundColour != null) result.addProperty("imageBackgroundColor", imageBackgroundColour);
		if (title != null) result.addProperty("title", title);
		result.addProperty("text", text);
		if (defaultAction != null) result.add("defaultAction", defaultAction.getAsJsonObject());
		// Add the JSON representations of the column actions to the actiosn array
		if (actions != null) {
			JsonArray actionArr = new JsonArray();
			for (Action a : actions) {
				actionArr.add(a.getAsJsonObject());
			}
			result.add("actions", actionArr);
		}
		return result;
	}

	/**
	 * Returns the JSON representation of the column object as a String
	 * @return	The string containing the JSON data of the column object
	 */
	public String getAsJsonString() {
		return getAsJsonObject().toString();
	}
	
}
