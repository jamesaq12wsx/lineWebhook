package com.pershing.action;

import com.google.gson.JsonObject;

/**
 * Interface that represents an action object of the line messages API
 * 	- See more documentation @ https://developers.line.me/en/docs/messaging-api/reference/#action-objects
 * 
 * @author ianw3214
 *
 */
public interface Action {

	public ActionType type();
	public JsonObject getAsJsonObject();
	public String getAsJsonString();
	
}
