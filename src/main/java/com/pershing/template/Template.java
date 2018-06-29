package com.pershing.template;

import com.google.gson.JsonObject;

/**
 * Interface that represents a template object in the Line messages API
 * 
 * @author ianw3214
 *
 */
public interface Template {

	public TemplateType type();	
	public JsonObject getAsJsonObject();
	public String getAsJsonString();
	
}
