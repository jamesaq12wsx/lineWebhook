package com.pershing.APIdemo;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pershing.action.PostbackAction;
import com.pershing.action.URIAction;
import com.pershing.dialogue.RootDialogue;
import com.pershing.event.MessageEvent;
import com.pershing.event.PostbackEvent;
import com.pershing.event.WebHookEvent;
import com.pershing.event.WebHookEventType;
import com.pershing.lineAPI.BaseWebHookHandler;
import com.pershing.message.MessageType;
import com.pershing.message.TemplateMessage;
import com.pershing.message.TextMessage;
import com.pershing.template.ButtonsTemplate;
import com.pershing.util.Util;

public class ChatbotWebHook extends BaseWebHookHandler {

	private static final String CHATBOT_MENU_URL = "https://chatbotapipsc.azurewebsites.net/api/chatbot/menu/top";
	private static final String CHATBOT_API_URL = "https://chatbotapipsc.azurewebsites.net/api/chatbot/";
	private static final String RICH_MENU_ID = "richmenu-68348ab1daabbd7e87b16806b314d115";
	
	private Map<String, UserData> users;
	
	public ChatbotWebHook(String channelSecret, String channelAccessToken) {
		super(channelSecret, channelAccessToken);
		users = new HashMap<String, UserData>();
	}
	
	@Override
	protected void handleEvent(String userId, WebHookEvent event) {
		// Call the follow event handler if the event is a follow event
		if (event.type() == WebHookEventType.FOLLOW) {
			sendWelcomeMessage(userId);
			messageSender.linkRichMenu(RICH_MENU_ID, userId);
			if (!users.containsKey(userId)) users.put(userId, new UserData());			
		}
		// Call the text message handler if the event is a text message
		if (event.type() == WebHookEventType.MESSAGE) {
			MessageEvent messageEvent = (MessageEvent) event;
			if (messageEvent.message().type() == MessageType.TEXT) {
				TextMessage textMessage = (TextMessage) messageEvent.message();
				handleTextMessage(textMessage.getText(), userId);
			}
		}
		if (event.type() == WebHookEventType.POSTBACK) {
			PostbackEvent postbackEvent = (PostbackEvent) event;
			handlePostbackEvent(postbackEvent, userId);
		}
	}
	
	private final void sendWelcomeMessage(String userId) {
		// First send a get request to chatbot API to get initial menu
		JsonObject response = HttpUtils.sendGet(CHATBOT_MENU_URL, null);
		if (response == null) {
			log(">>> [ChatbotWebHook] ERROR, initial menu failed to load");
			return;
		}
		try {
			JsonArray nodes = response.getAsJsonArray("nodes");
			ButtonsTemplate.ButtonsTemplateBuilder builder = 
					new ButtonsTemplate.ButtonsTemplateBuilder("請選擇一個選項");
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
			TemplateMessage message = new TemplateMessage("輸入help以開始使用", buttons);
			Util.sendSinglePush(messageSender, userId, message);
		} 
		catch (ParseException e) { e.printStackTrace(); }
	}
	
	private final void handleTextMessage(String message, String userId) {
		// TODO: Handle incoming text messages
	}
	
	private final void handlePostbackEvent(PostbackEvent event, String userId) {
		String data = event.postbackData();
		Map<String, String> parameters = Util.getQueryStringAsMap(data);
		// Parse the data for specific predefined actions
		if (parameters.containsKey("action")) {
			String action = parameters.get("action");
			// TODO: Handle the action postback thingys
		}
		// Parse the data for forward nodes to interact with chatbot API
		if (parameters.containsKey("forward")) {
			String forward = parameters.get("forward");
			String paramData = parameters.containsKey("data") ? parameters.get("data") : "";
			handleMessage(forward, paramData, userId);
		}
	}
	
	private void handleMessage(String message, String userId) {
		if (users.containsKey(userId)) {
			String nodeId = users.get(userId).nextNodeId;
			handleMessage(nodeId, message, userId);
		} else {
			handleMessage("", message, userId);
		}
	}
	
	private void handleMessage(String nodeId, String message, String userId) {
		if (!users.containsKey(userId)) users.put(userId, new UserData());
		UserData user = users.get(userId);
		JsonObject response = ruleEngineRequest(nodeId, message, user.currentToken, userId);
		if (response == null) {
			Util.sendSingleTextPush(messageSender, userId, "對不起，無法理解訊息");
			return;
		}
		// Fill the user data with new information
		UserData newUserData = new UserData();
		if (response.get("token").isJsonPrimitive()) {
			newUserData.currentToken = response.get("token").getAsString();
		}
		// If a response message exists, respond with it
		if (!response.get("message").isJsonNull()) {
			String responseMessage = response.get("message").getAsString();
			if (!responseMessage.equals("")) {
				Util.sendSingleTextPush(messageSender, userId, responseMessage);
				// For now, assume having a response message means we are expecting input
				newUserData.expectingInput = true;
				JsonArray nodes = response.getAsJsonArray("nodes");
				if (nodes.isJsonArray() && nodes.size() > 0) {
					JsonObject node = nodes.get(0).getAsJsonObject();
					newUserData.nextNodeId = node.get("forward").getAsString();
					// PARSE THE NODES NO MATTER WHAT FOR NOW
				}
			}
		}
		JsonArray nodes = response.getAsJsonArray("nodes");
		if (nodes.size() > 0) {
			handleNodes(nodes, userId);	
		}
		users.put(userId, newUserData);
	}
	
	private final JsonObject ruleEngineRequest(String nodeId, String message, String token, String userId) {
		// Construct request body
		JsonObject body = new JsonObject();
		body.addProperty("channel", "line");
		body.addProperty("messagetype", "text");
		if (nodeId != null && !nodeId.equals("")) body.addProperty("nodeid", nodeId);
		body.addProperty("message", message);
		body.addProperty("userid", userId);
		
		// Construct headers
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		if (token != null && !token.equals("")) headers.put("Authorization", "Bearer " + token);
		
		// Send the request and return the result
		return HttpUtils.sendPost(CHATBOT_API_URL, headers, body);
	}
	
	// Helper method to interact with the user based on the input nodes
	private void handleNodes(JsonArray nodes, String userId) {
		try {
			for (JsonElement e : nodes) {
				JsonObject node = e.getAsJsonObject();
				String typeString = node.get("nodetype").getAsString();
				List<String> types = Arrays.asList(typeString.split(","));
				if (types.contains("D") || types.contains("DD")) {
					// print a menu with the next nodes as options
					ButtonsTemplate buttons = new ButtonsTemplate.ButtonsTemplateBuilder(
							node.get("nodetitle").getAsString()).build();
					buttons.addAction(new PostbackAction(
							node.get("content").getAsString(),
							"forward=" + node.get("forward").getAsString(),
							"\u200B" + node.get("content").getAsString()));
					TemplateMessage message = new TemplateMessage(node.get("content").getAsString(), buttons);
					Util.sendSinglePush(messageSender, userId, message);
				}
				if (types.contains("B")) {
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
								"\u200B" + buttonObject.get("title")));
					}
					TemplateMessage message = new TemplateMessage(node.get("nodetitle").getAsString(), buttons);
					Util.sendSinglePush(messageSender, userId, message);
				}
				if (types.contains("L")) {
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
					Util.sendSinglePush(messageSender, userId, message);
				}
				if (types.contains("QS") || types.contains("Q")) {
					// TODO: figure out something to do here?
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
