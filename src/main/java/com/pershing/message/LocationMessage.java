package com.pershing.message;

import com.google.gson.JsonObject;
import com.pershing.quickReply.QuickReply;

/**
 * A location message represents a message that contains location data
 * 	- See documentation @ https://developers.line.me/en/reference/messaging-api/#location-message
 * 
 * @author ianw3214
 *
 */
public class LocationMessage implements Message {

	// The title of the location message
	private final String title;
	// The address of the location message
	private final String address;
	// The coordinates of the location message
	private final float latitude;
	private final float longitude;
	
	/**
	 * Type getter method for calling program to handle messages correctly
	 */
	@Override
	public MessageType type() {
		return MessageType.LOCATION;
	}

	/**
	 * Constructor to create a location message with custom data
	 * 
	 * @param title			The title of the location message
	 * @param address		The address of the location message
	 * @param latitude		The latitude of the location message
	 * @param longitude		The longitude of the location message
	 */
	public LocationMessage(String title, String address, float latitude, float longitude) {
		this.title = title;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	/**
	 * Constructor to create convert location json data into a location message class
	 * 
	 * @param obj	The data of the location message to convert
	 */
	public LocationMessage(JsonObject obj) {
		System.out.println("PARSING: " + obj.toString() + "AS LOCATION MESSAGE");
		this.title = obj.has("title") ? obj.get("title").getAsString() : "";
		this.address = obj.get("address").getAsString();
		this.latitude = obj.get("latitude").getAsFloat();
		this.longitude = obj.get("longitude").getAsFloat();
	}
	
	@Override
	public void setQuickReply(QuickReply reply) {
		// TODO: implement this!	
	}

	/**
	 * Returns the JSON representation of the flex message
	 * @return	the JSON representation of the message
	 */
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

	/**
	 * Returns the JSON representation of the flex message as a String
	 * @return	The string containing the JSON data of the message
	 */
	@Override
	public String getAsJsonString() {
		return getAsJsonObject().toString();
	}
	
	// GETTER METHODS
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
