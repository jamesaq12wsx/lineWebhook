package com.pershing.message;

import com.google.gson.JsonObject;
import com.pershing.quickReply.QuickReply;
import com.pershing.template.Template;

public class TemplateMessage implements Message {

	private String altText;
	private Template template;
	private QuickReply reply;
	
	/**
	 * Type getter method for calling program to handle messages correctly
	 */
	public MessageType type() {
		return MessageType.TEMPLATE;
	}

	// default constructor
	public TemplateMessage() {
		altText = "";
		template = null;
	}
	
	// full constructor
	public TemplateMessage(String text, Template templateObject) {
		altText = text;
		template = templateObject;
	}
	
	/**
	 * Returns the JSON representation of the template message
	 * @return	the JSON representation of the message
	 */
	public JsonObject getAsJsonObject() {
		// if there is no attached template, return an empty object
		if (template == null) {
			return null;
		}
		JsonObject obj = new JsonObject();
		obj.addProperty("type", "template");
		obj.addProperty("altText", altText);
		obj.add("template", template.getAsJsonObject());
		if (reply != null) obj.add("quickReply", reply.getAsJsonObject());
		return obj;
	}

	/**
	 * Returns the JSON representation of the template message as a String
	 * @return	The string containing the JSON data of the message
	 */
	public String getAsJsonString() {
		return getAsJsonObject().toString();
	}

	@Override
	public void setQuickReply(QuickReply reply) {
		this.reply = reply;
	}

}
