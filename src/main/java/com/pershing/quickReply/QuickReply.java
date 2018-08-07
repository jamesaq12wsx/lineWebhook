package com.pershing.quickReply;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

public class QuickReply {

	private List<QuickReplyItem> items;
	
	public QuickReply() {
		items = new ArrayList<QuickReplyItem>();
	}
	
	public void addItem(QuickReplyItem item) {
		items.add(item);
	}
	
	public final JsonObject getAsJsonObject() {
		JsonObject result = new JsonObject();
		JsonArray items = new JsonArray();
		for (QuickReplyItem item : this.items) {
			items.add(item.getAsJsonObject());
		}
		result.add("items", items);
		return result;
	}
	
	public final String getAsJsonString() {
		return getAsJsonObject().toString();
	}
	
	
}
