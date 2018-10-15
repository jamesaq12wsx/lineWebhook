package com.pershing.event;

import com.google.gson.JsonObject;

/**
 * Class representing the source of webhook events
 * 
 * @author ianw3214
 *
 * TODO:
 *	- Further separate the 3 different source types into subclasses 
 *
 */
public class WebHookSource {

	private final WebHookSourceType type;
	private String userId;
	private String groupId;
	private String roomId;
	
	/**
	 * Main constructor, the Id field to set is dependent on the source type
	 * 
	 * @param type		The type of the source (USER, GROUP, ROOM)
	 * @param mainId	The main ID to be set
	 */
	public WebHookSource(WebHookSourceType type, String mainId) {
		this.type = type;
		// set the Id corresponding to the type of the source
		switch(type) {
		case USER: {
			userId = mainId;
		} break;
		case GROUP: {
			groupId = mainId;
		} break;
		case ROOM: {
			roomId = mainId;
		} break;
		default: {
			// This should never happen, as there are only 3 enumeration types
			// TODO: notify the user of an error somehow
		} break;
		}
	}
	
	/**
	 * Constructor that also accepts userId, used when more than the main Id is received
	 * 	- Note that if the main constructor sets the userId, it will be overwritten by
	 * 	 	the value in the parameter.
	 * 
	 * @param type		The type of the source (USER, GROUP, ROOM)
	 * @param mainId	The main ID to be set
	 * @param userId	The user ID to be set
	 */
	public WebHookSource(WebHookSourceType type, String mainId, String userId) {
		this(type, mainId);
		this.userId = userId;
	}

	/**
	 * Parser function for a JSON object representing the source of an event
	 * 	- If the source could not be parsed, a null reference is returned instead
	 * 
	 * @param src	The source JSON object
	 * @return		The source represented with a WebHookSource object
	 */
	public static WebHookSource buildFromJson(JsonObject src) {
		// parse the source object based on its type
				String type = src.get("type").getAsString();
				String userId = src.get("userId").getAsString();
				if (type.equals("user")) {
					if (userId == null) {
						return null;
					}
					return new WebHookSource(WebHookSourceType.USER, userId);
				}
				if (type.equals("group")) {
					String groupId = src.get("groupId").getAsString();
					if (groupId == null) {
						return null;
					}
					if (userId == null) {
						return new WebHookSource(WebHookSourceType.GROUP, groupId, userId);
					} else {
						return new WebHookSource(WebHookSourceType.GROUP, groupId);
					}
				}
				if (type.equals("room")) {
					String roomId = src.get("roomId").getAsString();
					if (roomId == null) {
						return null;
					}
					if (userId == null) {
						return new WebHookSource(WebHookSourceType.GROUP, roomId, userId);
					} else {
						return new WebHookSource(WebHookSourceType.GROUP, roomId);
					}
				}
				return null;
	}
	
	/**
	 * Getter function for the type of current source
	 * @return	The type of the current source
	 */
	public WebHookSourceType type() {
		return type;
	}
	
	/**
	 * Getter function for the ID of the type of current source
	 * @return	The id of the source type
	 */
	public final String getId() {
		switch(type) {
		case USER: return userId;
		case GROUP: return groupId;
		case ROOM: return roomId;
		default: return userId;	// THIS SHOULD NEVER HAPPEN
		}
	}
	
	/**
	 * Getter function for the userId of the current source
	 * @return	The userId
	 */
	public final String getUserId() {
		return userId;
	}
	
}
