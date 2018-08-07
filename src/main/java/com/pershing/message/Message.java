package com.pershing.message;

import com.google.gson.JsonObject;
import com.pershing.quickReply.QuickReply;

public interface Message {

	public MessageType type();
	public void setQuickReply(QuickReply reply);
	public JsonObject getAsJsonObject();
	public String getAsJsonString();
	
}
