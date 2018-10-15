package com.pershing.APIdemo;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pershing.action.PostbackAction;
import com.pershing.action.URIAction;
import com.pershing.message.TemplateMessage;
import com.pershing.sender.MessageSender;
import com.pershing.template.ButtonsTemplate;
import com.pershing.util.Util;

/**
 * A utility class that handles nodes returned from the chatbot API
 * 
 * @author ianw3214
 *
 */
public class ChatbotNodeHandler {

	/**
	 * Handle a default node (type 'D' or 'DD')
	 * 	- Default nodes are handled by just providing a button to go to the next node
	 * 
	 * @param node		The JSON data of the default node
	 * @param userId	The userId associated with the current node state
	 * @return			The message that should be sent to the user
	 */
	public static TemplateMessage handleDefaultNode(JsonObject node, String userId) {
		// print a menu with the next nodes as options
		ButtonsTemplate buttons = new ButtonsTemplate.ButtonsTemplateBuilder(
				node.get("nodetitle").getAsString()).build();
		buttons.addAction(new PostbackAction(
				node.get("content").getAsString(),
				"forward=" + node.get("forward").getAsString(),
				"\u200B" + node.get("content").getAsString()));
		TemplateMessage message = new TemplateMessage(node.get("content").getAsString(), buttons);
		return message;
	}
	
	/**
	 * Handle a buttons node (type 'B')
	 * 	- Buttons nodes are handled by providing a list of buttons to send to the user 
	 * 
	 * @param node		The JSON data of the buttons node
	 * @param userId	The userId associated with the current node state
	 * @return			The message that should be sent to the user
	 */
	public static TemplateMessage handleButtonsNode(JsonObject node, String userId) {
		// If the format is wrong, just exit the function
		if (!node.get("content").isJsonArray()) return null;
		// print a menu with the specified buttons
		ButtonsTemplate buttons = new ButtonsTemplate.ButtonsTemplateBuilder(
				node.get("nodetitle").getAsString()).build();
		JsonArray content = node.getAsJsonArray("content");
		for (JsonElement button : content) {
			JsonObject buttonObject = button.getAsJsonObject();
			buttons.addAction(new PostbackAction(
					buttonObject.get("title").getAsString(), 
					"forward=" + buttonObject.get("forward").getAsString() +
					"&data=" + buttonObject.get("customValue").getAsString(),
					"\u200B" + buttonObject.get("title").getAsString()));
		}
		TemplateMessage message = new TemplateMessage(node.get("nodetitle").getAsString(), buttons);
		return message;
	}
	
	/**
	 * Handle a link node (type 'L')
	 * 	- Links can either be returned in the form of an array or a single link, so both cases
	 * 		must be hanlded by the method since they both use the 'L' node type
	 * 
	 * @param node		The JSON data of the link node
	 * @param userId	The userId associated with the current node state
	 * @return			The message that should be sent to the user
	 */
	public static TemplateMessage handleLinkNode(JsonObject node, String userId) {
		// print a menu with the specified buttons
		ButtonsTemplate buttons = new ButtonsTemplate.ButtonsTemplateBuilder(
				node.get("nodetitle").getAsString()).build();
		JsonElement content = node.get("content");
		if (content.isJsonArray()) {
			// HANDLE THE LINK AS AN ARRAY
			JsonArray contentArray = node.getAsJsonArray("content");
			for (JsonElement button : contentArray) {
				JsonObject buttonObject = button.getAsJsonObject();
				buttons.addAction(new URIAction(
						buttonObject.get("title").getAsString(), 
						buttonObject.get("url").getAsString()));
			}
		} else {
			// HANDLE THE LINK AS A NORMAL FORWARD
			buttons.addAction(new URIAction(
					node.get("nodetitle").getAsString(),	
					node.get("content").getAsString()));
		}
		TemplateMessage message = new TemplateMessage(node.get("nodetitle").getAsString(), buttons);
		return message;
	}
	
	/**
	 * Construct a menu to forward to a list of nodes
	 * 	- Used when more than one node is returned in the nodes array of the chatbot API
	 * 
	 * @param nodes		The nodes array in the chatbot API response
	 * @param userId	The userId to send the menu to
	 * @return			The menu that should be sent to the user
	 */
	public static TemplateMessage constructMenuFromNodes(JsonArray nodes, String userId) {
		ButtonsTemplate.ButtonsTemplateBuilder builder = new ButtonsTemplate.ButtonsTemplateBuilder("選擇一個選項");
		for (JsonElement e : nodes) {
			JsonObject node = e.getAsJsonObject();
			try {
				builder.addAction(new PostbackAction(
						node.get("nodetitle").getAsString(),
						"forward=" + node.get("nodeid").getAsString(),
						"\u200B" + node.get("nodetitle").getAsString()));
			} catch (Exception ex) {
				// skip the current iteration if something went wrong
				continue;
			}
		}
		ButtonsTemplate buttons = builder.build();
		return new TemplateMessage("輸入help以開始使用", buttons);
	}
	
}
