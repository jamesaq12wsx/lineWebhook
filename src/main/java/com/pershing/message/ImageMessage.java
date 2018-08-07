package com.pershing.message;

import com.google.gson.JsonObject;
import com.pershing.quickReply.QuickReply;

public class ImageMessage implements Message {

	private final String originalContentUrl;
	private final String previewImageUrl;
	
	public ImageMessage(String originalContentUrl, String previewImageUrl) {
		this.originalContentUrl = originalContentUrl == null ? "" : originalContentUrl;
		this.previewImageUrl = previewImageUrl == null ? "" : previewImageUrl;
	}
	
	@Override
	public MessageType type() {
		return MessageType.IMAGE;
	}

	@Override
	public JsonObject getAsJsonObject() {
		JsonObject result = new JsonObject();
		result.addProperty("type", "image");
		result.addProperty("originalContentUrl", originalContentUrl);
		result.addProperty("previewImageUrl", previewImageUrl);
		return result;
	}

	@Override
	public String getAsJsonString() {
		return getAsJsonObject().toString();
	}

	@Override
	public void setQuickReply(QuickReply reply) {
		// TODO Auto-generated method stub
		
	}

}
