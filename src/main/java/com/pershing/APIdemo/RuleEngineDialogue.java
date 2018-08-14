package com.pershing.APIdemo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pershing.action.LocationAction;
import com.pershing.action.MessageAction;
import com.pershing.action.PostbackAction;
import com.pershing.action.URIAction;
import com.pershing.dialogue.RootDialogue;
import com.pershing.event.MessageEvent;
import com.pershing.event.PostbackEvent;
import com.pershing.event.WebHookEvent;
import com.pershing.event.WebHookEventType;
import com.pershing.message.ImageMessage;
import com.pershing.message.LocationMessage;
import com.pershing.message.Message;
import com.pershing.message.MessageType;
import com.pershing.message.TextMessage;
import com.pershing.mockAPI.MockAPI;
import com.pershing.quickReply.QuickReply;
import com.pershing.sender.Response;
import com.pershing.message.TemplateMessage;
import com.pershing.template.ButtonsTemplate;
import com.pershing.util.Util;

/**
 * The dialogue used to interact with the backend chatbot API
 * 	- Chatbot API documentation @ https://bitbucket.org/PershingAppDev/chatbotapi/src/master/Doc/
 * 
 * @author ianw3214
 *
 */
public class RuleEngineDialogue extends RootDialogue {

	// The ID of the rich menu to link to users
	private static final String RICH_MENU_ID = "richmenu-949e4ac74b5b932f062ef11d20316c32";
	
	// The endpoints of the chatbot API
	private static final String CHATBOT_API_URL = "https://chatbotapipsc.azurewebsites.net/api/chatbot/";
	private static final String CHATBOT_MENU_URL = "https://chatbotapipsc.azurewebsites.net/api/chatbot/menu/top";
	
	private static final String LIFF_APP_URL = "line://app/1599437019-DalpOzwK";
	
	// State variables to keep track where the user is in the nodTree
	private boolean expectingInput;
	private String nextNodeId;
	private String currentToken;
	
	private boolean verified;
	
	/**
	 * 
	 */
	public RuleEngineDialogue() {
		this.expectingInput = false;
		this.nextNodeId = "";
		this.currentToken = "";
		this.verified = false;
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
		System.out.println("VERIFIED: " + Boolean.toString(verified));
		if (verified) {
			// Handle the event based on its type
			if (event.type() == WebHookEventType.MESSAGE) {
				MessageEvent messageEvent = (MessageEvent) event;
				if (messageEvent.message().type() == MessageType.TEXT) {
					TextMessage textMessage = (TextMessage) messageEvent.message();
					handleTextMessageEvent(textMessage, userId);	
				}
				if (messageEvent.message().type() == MessageType.LOCATION) {
					// JUST USE THE ADDRESS AS A MESSAGE FOR NOW
					LocationMessage locationMessage = (LocationMessage) messageEvent.message();
					handleMessage(nextNodeId, locationMessage.getAddress(), currentToken, userId);
				}
			}
			if (event.type() == WebHookEventType.FOLLOW) {
				sendInitialMessage(userId);
				// Also link the rich menu to the user
				sender.linkRichMenu(RICH_MENU_ID, userId);
			}
			if (event.type() == WebHookEventType.POSTBACK) {
				PostbackEvent postbackEvent = (PostbackEvent) event;
				handlePostbackEvent(postbackEvent, userId);
			}
		} else {
			if (event.type() != WebHookEventType.UNFOLLOW) {
				handleVerification(userId);	
			}
		}
	}
	
	// Helper method to interact with the user based on the input nodes
	private void handleNodes(JsonArray nodes, String userId) {
		try {
			if (nodes.size() == 1) {
				for (JsonElement e : nodes) {
					JsonObject node = e.getAsJsonObject();
					String typeString = node.get("nodetype").getAsString();
					List<String> types = Arrays.asList(typeString.split(","));
					// STORE THE MESSAGES BEFORE SENDING THEM SO THEY CAN BE SENT AT ONCE
					List<Message> messages = new ArrayList<Message>();
					if (types.contains("D") || types.contains("DD")) {
						TemplateMessage message = ChatbotNodeHandler.handleDefaultNode(node, userId);
						if (message != null) messages.add(message);
					}
					if (types.contains("B")) {
						TemplateMessage message = ChatbotNodeHandler.handleButtonsNode(node, userId);
						if (message != null) messages.add(message);
					}
					if (types.contains("L")) {
						TemplateMessage message = ChatbotNodeHandler.handleLinkNode(node, userId);
						if (message != null) messages.add(message);
					}
					if (types.contains("QS") || types.contains("Q")) {
						// TODO: figure out something to do here?
					}
					if (types.contains("LO")) {
						// SEND A LOCATION QUICKREPLY LMAO
						if (messages.size() > 0) {
							QuickReply reply = new QuickReply();
							reply.addItem(new LocationAction("發送位置"));
							messages.get(0).setQuickReply(reply);
						}
						// TODO: Figure out a better way to do this
						// SET THE NEXT NODE
						nextNodeId = node.get("forward").getAsString();
					}
					Response resp = sender.sendPush(userId, messages, "");
					System.out.println("LINEAPI RESPONSE: " + resp.status() + " => " + resp.message());
				}	
				
			}
			if (nodes.size() > 1) {
				TemplateMessage menu = ChatbotNodeHandler.constructMenuFromNodes(nodes, userId);
				Util.sendSinglePush(sender, userId, menu);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Helper method to handle a text message
	private void handleTextMessageEvent(TextMessage message, String userId) {
		String text = message.getText();
		// INTERCEPT THIS MESSAGE IF DETECTED!!!!!!
		if (text.toLowerCase().equals("id")) {
			Util.sendSingleTextPush(sender, userId, "USER ID: " + userId);
			return;
		}
		if (expectingInput) {
			handleMessage(nextNodeId, text, currentToken, userId);
		} else {
			Util.sendSingleTextPush(sender, userId, "對不起，無法解析輸入");
		}
	}
	
	// Helper method to handle a postback event
	private void handlePostbackEvent(PostbackEvent event, String userId) {
		String data = event.postbackData();
		// parse the data as an action trigger if the data specifies it
		Map<String, String> parameters = Util.getQueryStringAsMap(data);
		if (parameters.containsKey("action")) {
			String action = parameters.get("action");
			System.out.println(">>> POSTBACK DATA ACTION: " + action);
			if (action.equals("account")) {
				handleMessage("1.1", "", currentToken, userId);
			}
			if (action.equals("qr")) {
				push(new QRCodeDialogue(userId, RICH_MENU_ID));
			}
			if (action.equals("exchange")) {
				if (parameters.containsKey("data")) {
					String currency = parameters.get("data");
					float result = MockAPI.getCurrency(currency, "TWD");
					Util.sendSingleTextPush(sender, userId, "1 TWD = " + Float.toString(result) + currency);
				} else {
					DemoUtils.sendCurrencyExchangeCarousel(userId, sender);	
				}
			}
		}
		if (parameters.containsKey("forward")) {
			String forward = parameters.get("forward"); 
			String paramData = parameters.containsKey("data") ? parameters.get("data") : "";
			System.out.println(">>> POSTBACK FORWARD: " + forward);
			handleMessage(forward, paramData, currentToken, userId);
		}
	}

	/**
	 * Helper method to handle a message to the chatbot API
	 * @param nodeId		The nodeId to make the rule engine request with
	 * @param message		The message to make the rule engine request with
	 * @param token			The token to make the rule engine request with
	 * @param userId		The userId to make the rule engine request with
	 * @param handleNodes	Flag to indicate whether to parse the nodes array or not
	 */
	private void handleMessage(String nodeId, String message, String token, String userId, boolean handleNodes) {
		// If the first character is a zero width space, DON'T PARSE THE MESSAGE
		if (message.length() > 0 && message.substring(0, 1).equals("\u200B")) {
			return;
		}
		// Then get the response from the chatbot API and parse it
		JsonObject response = ruleEngineRequest(nodeId, message, token, userId);
		// Clear current state before setting it again
		currentToken = "";
		expectingInput = false;
		nextNodeId = "";
		if (response == null) {
			Util.sendSingleTextPush(sender, userId, "對不起，無法理解訊息.");
			return;
		}
		// Get the token first if it exists
		if (response.has("token") && !response.get("token").isJsonNull()) {
			currentToken = response.get("token").getAsString();
		}
		// If a response message exists, just respond with THAT
		if (response.has("message") && !response.get("message").isJsonNull()) {
			String responseMessage = response.get("message").getAsString();
			expectingInput = true;
			// Only print out buttons if there is a message to use as the title
			if (response.has("content")) {
				handleNodes = handleResponseContent(response, userId, responseMessage);
			}
			// Set the next node if it exists
			JsonArray nodes = response.getAsJsonArray("nodes");
			if (nodes != null && nodes.isJsonArray() && nodes.size() > 0) {
				JsonObject node = nodes.get(0).getAsJsonObject();
				if (node.has("forward") && !node.get("forward").isJsonNull()) {
					nextNodeId = node.get("forward").getAsString();	
				}
			}
		}
		if (handleNodes) {
			JsonArray nodes = response.getAsJsonArray("nodes");
			if (nodes.size() > 0) {
				handleNodes(nodes, userId);	
			} else {
				sendInitialMessage(userId);
			}	
		}
	}
	
	// Helper method to handle a message to the chatbot API
	private void handleMessage(String nodeId, String message, String token, String userId) {
		// Handle the message nodes by default
		handleMessage(nodeId, message, token, userId, true);
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
		
		System.out.println("> DEBUG: " + ">>> SENDING REQUEST W/ BODY: " + obj.toString());
		
        // request headers
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        if (token != null && !token.equals("")) headers.put("Authorization", "Bearer " + token);
        
        JsonObject response = HttpUtils.sendPost(CHATBOT_API_URL, headers, obj);
        if (response != null) System.out.println("> DEBUG: " + response.toString());
        return response;
	}
	
	// Helper method to send initial menu to user for a list of actions
	private void sendInitialMessage(String userId) {
		JsonObject response = HttpUtils.sendGet(CHATBOT_MENU_URL, null);
		if (response == null) return;
		if (!response.has("nodes") || !response.get("nodes").isJsonArray()) return;
		// Construct the initial menu from the nodes data
		JsonArray nodes = response.getAsJsonArray("nodes");
		TemplateMessage menu = ChatbotNodeHandler.constructMenuFromNodes(nodes, userId);
		Util.sendSinglePush(sender, userId, menu);
	}
	
	public boolean handleResponseContent(JsonObject response, String userId, String responseMessage) {
		List<String> contentType = new ArrayList<String>();
		if (response.has("contentType") && response.get("contentType").isJsonPrimitive()) {
			contentType = Arrays.asList(response.get("contentType").getAsString().split(","));
		}
		List<Message> messages = new ArrayList<Message>();
		boolean handleNodes = true;
		if (contentType.contains("LO")) {
			// send location messages independently of the other messages
			Util.sendSingleTextPush(sender, userId, responseMessage);
			// Go through the locations and add them one by one
			List<Message> locationMessages = new ArrayList<Message>();
			JsonArray content = response.getAsJsonArray("content");
			for (JsonElement e : content) {
				JsonObject locationObject = e.getAsJsonObject();
				LocationMessage message = new LocationMessage(
							locationObject.get("title").getAsString(),
							locationObject.get("address").getAsString(),
							locationObject.get("lat").getAsFloat(),
							locationObject.get("lng").getAsFloat()
						);
				locationMessages.add(message);
			}
			sender.sendPush(userId, locationMessages, "");
			handleNodes = false;
		}
		if (contentType.contains("L")) {
			ButtonsTemplate buttons = new ButtonsTemplate.ButtonsTemplateBuilder(responseMessage).build();
			if (response.get("content").isJsonArray()) {
				JsonArray content = response.getAsJsonArray("content");
				for (JsonElement e : content) {
					JsonObject obj = e.getAsJsonObject();
					try {
						buttons.addAction(new URIAction(
									obj.get("title").getAsString(),
									obj.get("url").getAsString()
								));
					} catch (Exception ex) {}
				}
			}
			if (response.get("content").isJsonObject()) {
				JsonObject content = response.getAsJsonObject("content");
				buttons.addAction(new URIAction(
							content.get("title").getAsString(),
							content.get("url").getAsString()
						));
			}
			messages.add(new TemplateMessage(responseMessage, buttons));
			handleNodes = false;
		}
		if (contentType.contains("B")) {
			if (response.has("content") && response.get("content").isJsonArray()) {
				ButtonsTemplate buttons = new ButtonsTemplate.ButtonsTemplateBuilder(responseMessage).build();
				JsonArray content = response.getAsJsonArray("content");
				for (JsonElement e : content) {
					JsonObject obj = e.getAsJsonObject();
					try {
						buttons.addAction(new PostbackAction(
									obj.get("title").getAsString(),
									"forward=" + obj.get("forward").getAsString() +
									(obj.has("customValue") ?  "&data=" + obj.get("customValue").getAsString() : 
										(obj.has("value") ? "&data=" + obj.get("value").getAsString() : "")),
									"\u200B" + obj.get("title")
								));
					} catch (Exception ex) {}
				}
				messages.add(new TemplateMessage(responseMessage, buttons));
			} else {
				messages.add(new TextMessage(responseMessage));
			}
			handleNodes = false;
		}
		if (contentType.contains("I")) {
			messages.add(new TextMessage(responseMessage));
		}
		if (contentType.contains("Q")) {
			messages.add(new TextMessage(responseMessage));
			handleNodes = false;
		}
		sender.sendPush(userId, messages, "");
		return handleNodes;
	}
	
	private void handleVerification(String userId) {
		// Unlink the rich menu until the user is verified
		sender.UnlinkRichMenu(userId);
		// Call node 9 to handle account binding
		JsonObject response = ruleEngineRequest("9", "", "", userId);
		if (response.has("contentType")) {
			if (response.get("contentType").isJsonPrimitive() &&
					response.get("contentType").getAsString().equals("I")) 
			{
				verified = true;
				sendInitialMessage(userId);
				sender.linkRichMenu(RICH_MENU_ID, userId);
				return;
			} else {
				// TODO: this is inefficient, fix it!
				handleMessage("9", "", "", userId, false);
			}
		}
		/*
		ButtonsTemplate buttons = new ButtonsTemplate.ButtonsTemplateBuilder("帳戶尚未綁定").build();
		buttons.addAction(new URIAction("綁定", LIFF_APP_URL));
		Util.sendSinglePush(sender, userId, new TemplateMessage("帳戶尚未綁定", buttons));
		*/
	}
	
}
