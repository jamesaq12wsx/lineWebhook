package com.pershing.message;

import com.google.gson.JsonObject;

public interface Message {

	public MessageType type();
	public JsonObject getAsJsonObject();
	public String getAsJsonString();
	
}
