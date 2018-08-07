package com.pershing.quickReply;

import com.google.gson.JsonObject;
import com.pershing.action.Action;

public class QuickReplyItem {

	private final Action action;
	private String imageUrl;
	
	public QuickReplyItem(Action action) {
		this.action = action;
	}
	
	public void setUrl(String url) {
		this.imageUrl = url;
	}
	
	public JsonObject getAsJsonObject() {
		JsonObject result = new JsonObject();
		result.addProperty("type", "action");
		if (imageUrl != null) {
			result.addProperty("imageUrl", imageUrl);
		}
		result.add("action", action.getAsJsonObject());
		return result;
	}
	
	public String getAsJsonString() {
		return getAsJsonObject().toString();
	}
	
}
