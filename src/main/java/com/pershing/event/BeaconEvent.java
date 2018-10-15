package com.pershing.event;

import com.google.gson.JsonObject;

/**
 * Class representing a BeaconEvent of the line Messaging API
 * 	- See more documentation @ https://developers.line.me/en/docs/messaging-api/reference/#beacon-event
 * 
 * TODO:
 * 	- implement a builder class since Beacon events have a lot of properties
 * 
 * @author ianw3214
 *
 */

public class BeaconEvent extends WebHookEvent {
	
	// Token for replying to this event
	private final String replyToken;
	// Hardware ID of the beacon that was detected
	private final String hwid;
	// Type of beacon event
	private final BeaconEventType beaconEventType;
	// Device message of beacon that was detected - NOT SUPPORTED BY ALL DEVICES
	private String deviceMessage;
	
	/**
	 * Main constructor that requires common properties be set
	 * 
	 * @param timestamp
	 * @param source
	 * @param replyToken
	 * @param hwid
	 * @param beaconEventType
	 */
	public BeaconEvent(long timestamp, WebHookSource source, String replyToken, String hwid, 
			BeaconEventType beaconEventType) 
	{
		super(WebHookEventType.BEACON, timestamp, source);
		this.replyToken = replyToken;
		this.hwid = hwid;
		this.beaconEventType = beaconEventType;
	}
	
	/**
	 * Constructor that directly sets beacon event properties by reading from an event JSON
	 * 	- Should be used to directly parse JSON received from the webhook
	 * 
	 * @param json
	 */
	public BeaconEvent(JsonObject json) {
		super(WebHookEventType.BEACON, json);
		String replyToken = json.get("replyToken").getAsString();
		this.replyToken = replyToken;
		String hwid = json.get("hwid").getAsString();
		this.hwid = hwid;
		// set the Beacon event type
		String beaconEventType = json.get("beaconEventType").getAsString();
		if (beaconEventType.equals("enter")) this.beaconEventType = BeaconEventType.ENTER;
		else if (beaconEventType.equals("leave")) this.beaconEventType = BeaconEventType.LEAVE;
		else if (beaconEventType.equals("banner")) this.beaconEventType = BeaconEventType.BANNER;
		else this.beaconEventType = BeaconEventType.ENTER;	// <- TODO: HANDLE THIS ERROR MORE ELEGANTLY
	}
	
	/**
	 * Constructor that sets all common properties as well as device message
	 * 	- use if device message is supported by the beacon
	 * 
	 * @param timestamp
	 * @param source
	 * @param replyToken
	 * @param hwid
	 * @param beaconEventType
	 * @param deviceMessage
	 */
	public BeaconEvent(long timestamp, WebHookSource source, String replyToken, String hwid, 
			BeaconEventType beaconEventType, String deviceMessage) 
	{
		this(timestamp, source, replyToken, hwid, beaconEventType);
		this.deviceMessage = deviceMessage;
	}
	
	/**
	 * Getter method for the event replyToken
	 * @return	The replyToken of the event
	 */
	public String replyToken() {
		return replyToken;
	}
	
	/**
	 * Getter method for the hardware ID
	 * @return	The hardware ID of the beacon
	 */
	public String hwid() {
		return hwid;
	}
	
	/**
	 * Getter method for the beacon event type
	 * @return	The beacon event type
	 */
	public BeaconEventType beaconEventType() {
		return beaconEventType;
	}
	
	/**
	 * Getter method for the beacon device message
	 * 	- An empty string is returned if dm is not supported on beacon
	 * 
	 * @return	The device message
	 */
	public String deviceMessage() {
		return deviceMessage == null ? "" : deviceMessage;
	}
	
}
