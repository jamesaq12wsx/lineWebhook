package com.pershing.message;

import com.google.gson.JsonObject;
import com.pershing.quickReply.QuickReply;

public class LocationMessage implements Message {

	private final String title;
	private final String address;
	private final float latitude;
	private final float longitude;
	
	@Override
	public MessageType type() {
		return MessageType.LOCATION;
	}

	public LocationMessage(String title, String address, float latitude, float longitude) {
		this.title = title;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public LocationMessage(JsonObject obj) {
		// MAY THROW EXCEPTION IF PARSED WRONG
		this.title = obj.get("title").getAsString();
		this.address = obj.get("address").getAsString();
		this.latitude = obj.get("latitude").getAsFloat();
		this.longitude = obj.get("longitude").getAsFloat();
	}
	
	@Override
	public void setQuickReply(QuickReply reply) {
		// TODO Auto-generated method stub		
	}

	@Override
	public JsonObject getAsJsonObject() {
		JsonObject result = new JsonObject();
		result.addProperty("type", "location");
		result.addProperty("title", title);
		result.addProperty("address", address);
		result.addProperty("latitude", latitude);
		result.addProperty("longitude", longitude);
		return result;
	}

	@Override
	public String getAsJsonString() {
		return getAsJsonObject().toString();
	}
	
	public String getTitle() {
		return this.title;
	}
	public String getAddress() {
		return this.address;
	}
	public float getLatitude() {
		return this.latitude;
	}
	public float getLongitude() {
		return this.longitude;
	}

}
