package com.pershing.quickReply;

import com.google.gson.JsonObject;

public class LocationQuickReply implements QuickReplyItem {

	private final String label;
	
	public LocationQuickReply(String label) {
		this.label = label;
	}
	
	@Override
	public QuickReplyType type() {
		return QuickReplyType.LOCATION;
	}

	@Override
	public JsonObject getAsJsonObject() {
		JsonObject result = new JsonObject();
		result.addProperty("type", "location");
		result.addProperty("label", label);
		return result;
	}

	@Override
	public String getAsJsonString() {
		return getAsJsonObject().toString();
	}

}
