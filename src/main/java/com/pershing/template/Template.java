package com.pershing.template;

import com.google.gson.JsonObject;

/**
 * Interface that represents a template object in the Line messages API
 * 
 * @author ianw3214
 *
 */
public interface Template {

	// A type getter function to differentiate between template types
	public TemplateType type();	
	// A getter function to convert template classes into a JSON object
	public JsonObject getAsJsonObject();
	// A getter function to convert template classes into a JSON object string
	public String getAsJsonString();
	
}
