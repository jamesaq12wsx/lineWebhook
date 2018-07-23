package com.pershing.template;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.pershing.action.Action;

public class Column {

	private String thumbnailImageUrl;
	private String imageBackgroundColour;
	private String title;
	private final String text;	// Required
	private Action defaultAction;
	private List<Action> actions;	// Max 3
	
	public Column(String text) {
		this.text = text;
	}
	
	public void setThumbnailImageUrl(String thumbnailImageUrl) {
		this.thumbnailImageUrl = thumbnailImageUrl;
	}
	
	public void setImageBackgroundcolour(String imageBackgroundColour) {
		this.imageBackgroundColour = imageBackgroundColour;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setDefaultAction(Action action) {
		this.defaultAction = action;
	}
	
	public void addAction(Action action) {
		if (this.actions == null) this.actions = new ArrayList<Action>();
		this.actions.add(action);
	}
	
	public int numActions() {
		return this.actions.size();
	}
	
	public JsonObject getAsJsonObject() {
		JsonObject result = new JsonObject();
		if (thumbnailImageUrl != null) result.addProperty("thumbnailImageUrl", thumbnailImageUrl);
		if (imageBackgroundColour != null) result.addProperty("imageBackgroundColor", imageBackgroundColour);
		if (title != null) result.addProperty("title", title);
		result.addProperty("text", text);
		if (defaultAction != null) result.add("defaultAction", defaultAction.getAsJsonObject());
		if (actions != null) {
			JsonArray actionArr = new JsonArray();
			for (Action a : actions) {
				actionArr.add(a.getAsJsonObject());
			}
			result.add("actions", actionArr);
		}
		return result;
	}

	public String getAsJsonString() {
		return getAsJsonObject().toString();
	}
	
}
