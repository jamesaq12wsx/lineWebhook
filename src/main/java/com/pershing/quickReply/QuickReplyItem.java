package com.pershing.quickReply;

import com.google.gson.JsonObject;

public interface QuickReplyItem {

	public QuickReplyType type();
	public JsonObject getAsJsonObject();
	public String getAsJsonString();
	
}
