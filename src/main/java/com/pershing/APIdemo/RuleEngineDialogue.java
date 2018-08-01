package com.pershing.APIdemo;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import com.pershing.message.MessageType;
import com.pershing.message.TemplateMessage;
import com.pershing.message.TextMessage;
import com.pershing.template.ButtonsTemplate;
import com.pershing.util.Util;

public class RuleEngineDialogue extends RootDialogue {

	private static final String richMenuId = "richmenu-e1b00252e15c21c6bed945d4ab1f1657";
	
	private static final String CHATBOT_API_URL = "https://chatbotapipsc.azurewebsites.net/api/chatbot/";
	private static final String CHATBOT_MENU_URL = "https://chatbotapipsc.azurewebsites.net/api/chatbot/menu/top";
	
	private boolean expectingInput;
	private String nextNodeId;
	private String currentToken;
	
	public RuleEngineDialogue() {
		this.expectingInput = false;
		this.nextNodeId = "";
		this.currentToken = "";
	}
	
	@Override
	public RootDialogue create() {
		return new RuleEngineDialogue();
	}

	@Override
	public void handleEvent(WebHookEvent event, String userId) {
		// PRINT CURRENT INFO
		System.out.println("EXPECTING INPUT: " + expectingInput);
		System.out.println("NEXT NODE ID: " + nextNodeId);
		System.out.println("CURRENT TOKEN : + currentToken");
		if (event.type() == WebHookEventType.MESSAGE) {
			MessageEvent messageEvent = (MessageEvent) event;
			if (messageEvent.message().type() == MessageType.TEXT) {
				TextMessage textMessage = (TextMessage) messageEvent.message();
				// INTERCEPT THIS MESSAGE IF DETECTED!!!!!!
				if (textMessage.getText().equals("QR") || textMessage.getText().equals("qr")) {
					String uuid = UUID.randomUUID().toString();
					String path = "./" + uuid + ".jpeg";
					try {
						QRCodeGenerator.generateQRCodeImage("TEST", 240, 240, path);
						String imagePath = "https://peaceful-plains-74132.herokuapp.com/" + uuid + ".jpeg";
						Util.sendSingleTextPush(sender, userId, imagePath);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (expectingInput) {
					handleMessage(nextNodeId, textMessage.getText(), currentToken, userId);
				} else {
					Util.sendSingleTextReply(sender, messageEvent.replyToken(), "Sorry, not expecting input.");
				}	
			}
		}
		if (event.type() == WebHookEventType.FOLLOW) {
			sendInitialMessage(userId);
			// Also link the rich menu to the user
			sender.linkRichMenu(richMenuId, userId);
		}
		if (event.type() == WebHookEventType.POSTBACK) {
			PostbackEvent postbackEvent = (PostbackEvent) event;
			handlePostbackEvent(postbackEvent, userId);
		}
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
					Util.sendSinglePush(sender, userId, message);
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
					Util.sendSinglePush(sender, userId, message);
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
					Util.sendSinglePush(sender, userId, message);
				}
				if (types.contains("QS") || types.contains("Q")) {
					// TODO: figure out something to do here?
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Helper method to handle a postback event
	private void handlePostbackEvent(PostbackEvent event, String userId) {
		String data = event.postbackData();
		System.out.println(">>> RECIEVING POSTBACK EVENT: " + data);
		// parse the data as an action trigger if the data specifies it
		Map<String, String> parameters = Util.getQueryStringAsMap(data);
		if (parameters.containsKey("action")) {
			String action = parameters.get("action");
			System.out.println(">>> POSTBACK DATA ACTION: " + action);
			if (action.equals("transfer")) {
				handleMessage("1.2", "", "", userId); 	
			}
			if (action.equals("rates")) {
				handleMessage("5	", "", "", userId);
			}
			if (action.equals("service")) {
				Util.sendSingleTextPush(sender, userId, "CUSTOMER SERVICE");
			}
		}
		if (parameters.containsKey("forward")) {
			String forward = parameters.get("forward"); 
			String paramData = parameters.containsKey("data") ? parameters.get("data") : "";
			System.out.println(">>> POSTBACK FORWARD: " + forward);
			handleMessage(forward, paramData, currentToken, userId);
		}
	}
	
	private void handleMessage(String nodeId, String message, String token, String userId) {
		// If the first character is a zero width space, DON'T PARSE THE MESSAGE
		if (message.length() > 0 && message.substring(0, 1).equals("\u200B")) {
			return;
		}
		JsonObject response = ruleEngineRequest(nodeId, message, token, userId);
		if (response == null) {
			Util.sendSingleTextPush(sender, userId, "Sorry, message could not be understood.");
		} else {
			try {
				// Get the token first if it exists
				if (response.get("token").isJsonNull()) {
					currentToken = "";	
				} else {
					currentToken = response.get("token").getAsString();
				}
				// If a response message exists, just respond with THAT
				if (!response.get("message").isJsonNull()) {
					String responseMessage= response.get("message").getAsString();
					if (!responseMessage.equals("")) {
						Util.sendSingleTextPush(sender, userId, responseMessage);
						expectingInput = true;
						JsonArray nodes = response.getAsJsonArray("nodes");
						if (nodes.isJsonArray() && nodes.size() > 0) {
							JsonObject node = nodes.get(0).getAsJsonObject();
							nextNodeId = node.get("forward").getAsString();
							// JUST PARSE THE NODES NO MATTER WHAT FOR NOW
							/*
							List<String> types = Arrays.asList(node.get("nodetype").getAsString().split(","));
							if (!types.contains("QS") && !types.contains("Q")) return;
							*/
						}
					}
				}
				JsonArray nodes = response.getAsJsonArray("nodes");
				if (nodes.size() > 0) {
					handleNodes(nodes, userId);	
				} else {
					sendInitialMessage(userId);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}
	}
	
	// Helper method to get the next node depending on the sent message from backend API
	private JsonObject ruleEngineRequest(String nodeId, String message, String token, String userId) {
		// construct the json object to be sent first
		JsonObject obj = new JsonObject();
		obj.addProperty("channel", "line");
		obj.addProperty("messagetype", "text");
		if (nodeId != null && !nodeId.equals("")) obj.addProperty("nodeid", nodeId);
		obj.addProperty("message", message);
		obj.addProperty("userid", userId);
		
		System.out.println(">>> SENDING REQUEST W/ BODY: " + obj.toString());
		
        // request headers
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        if (token != null && !token.equals("")) headers.put("Authorization", "Bearer " + token);
        
        return HttpUtils.sendPost(CHATBOT_API_URL, headers, obj);
	}
	
	// Helper method to send initial menu to user for a list of actions
	private void sendInitialMessage(String userId) {
		JsonObject response = HttpUtils.sendGet(CHATBOT_MENU_URL, null);
		// We know the response contains all the default nodes, no need to validate
		if (response != null) {
			try {
				JsonArray nodes = response.getAsJsonArray("nodes");
				// print a menu with the specified buttons
				ButtonsTemplate.ButtonsTemplateBuilder builder = new 
						ButtonsTemplate.ButtonsTemplateBuilder("Select an option to get started");
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
				TemplateMessage message = new TemplateMessage("Type help to get started", buttons);
				Util.sendSinglePush(sender, userId, message);
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}
	}
	
}
