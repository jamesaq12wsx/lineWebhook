package com.pershing.action;

import com.google.gson.JsonObject;

public class LocationAction implements Action {

	private final String label;
	
	public LocationAction(String label) {
		this.label = label;
	}
	
	@Override
	public ActionType type() {
		return ActionType.LOCATION;
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
