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
import com.pershing.action.PostbackAction;
import com.pershing.action.URIAction;
import com.pershing.dialogue.RootDialogue;
import com.pershing.event.MessageEvent;
import com.pershing.event.PostbackEvent;
import com.pershing.event.WebHookEvent;
import com.pershing.event.WebHookEventType;
import com.pershing.message.ImageMessage;
import com.pershing.message.Message;
import com.pershing.message.MessageType;
import com.pershing.message.TextMessage;
import com.pershing.message.TemplateMessage;
import com.pershing.mockAPI.Account;
import com.pershing.mockAPI.MockAPI;
import com.pershing.template.ButtonsTemplate;
import com.pershing.util.Util;

public class RuleEngineDialogue extends RootDialogue {

	private static final String richMenuId = "richmenu-949e4ac74b5b932f062ef11d20316c32";
	
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
		// Handle the event based on its type
		if (event.type() == WebHookEventType.MESSAGE) {
			MessageEvent messageEvent = (MessageEvent) event;
			if (messageEvent.message().type() == MessageType.TEXT) {
				TextMessage textMessage = (TextMessage) messageEvent.message();
				handleTextMessageEvent(textMessage, userId);	
			}
		}
		if (event.type() == WebHookEventType.FOLLOW) {
			sendInitialMessage(userId);
			// Also link the rich menu to the user
			sender.linkRichMenu(richMenuId, userId);
			// Also setup user data on mock API
			MockAPI.generateAccount(userId, "");
		}
		if (event.type() == WebHookEventType.POSTBACK) {
			PostbackEvent postbackEvent = (PostbackEvent) event;
			handlePostbackEvent(postbackEvent, userId);
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
					if (types.contains("D") || types.contains("DD")) {
						ChatbotNodeHandler.handleDefaultNode(node, userId, sender);
					}
					if (types.contains("B")) {
						ChatbotNodeHandler.handleButtonsNode(node, userId, sender);
					}
					if (types.contains("L")) {
						ChatbotNodeHandler.handleLinkNode(node, userId, sender);
					}
					if (types.contains("QS") || types.contains("Q")) {
						// TODO: figure out something to do here?
					}
				}	
			}
			if (nodes.size() > 1) {
				ChatbotNodeHandler.constructMenuFromNodes(nodes, userId, sender);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Helper method to handle a text message
	private void handleTextMessageEvent(TextMessage message, String userId) {
		String text = message.getText();
		// INTERCEPT THIS MESSAGE IF DETECTED!!!!!!
		if (text.equals("QR") || text.equals("qr")) {
			/*
			String uuid = UUID.randomUUID().toString();
			String path = "./" + uuid + ".jpeg";
			try {
				String imagePath = "https://peaceful-plains-74132.herokuapp.com/" + uuid + ".jpeg";
				QRCodeGenerator.generateQRCodeImage("TEST", 240, 240, path);
				Util.sendSingleTextPush(sender, userId, imagePath);
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}
			*/
			// Send a button for the user to bring up the LIFF app
			ButtonsTemplate.ButtonsTemplateBuilder builder = 
					new ButtonsTemplate.ButtonsTemplateBuilder("QR");
			builder.addAction(new URIAction("LIFF APP", "https://line.me/R/app/1588952156-kX2KV06z"));
			ButtonsTemplate buttons = builder.build();
			TemplateMessage qrTemplate = new TemplateMessage("QR Code link", buttons);
			Util.sendSinglePush(sender, userId, qrTemplate);
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
				if (parameters.containsKey("data")) {
					String accountId = parameters.get("data");
					List<Account> accounts = MockAPI.getUserAccounts(userId);
					for (Account account : accounts) {
						if (account.id.equals(accountId)) {
							String overview = account.name + '\n';
							overview += "ID: " + account.id + '\n';
							overview += "BALANCE: " + account.balance;
							// skip the history for now
							Util.sendSingleTextPush(sender, userId, overview);
							return;
						}
					}
				} else {
					// Print an overview of the accounts
					List<Account> accounts = MockAPI.getUserAccounts(userId);
					String reply = "ACCOUNTS:\n";
					ButtonsTemplate buttons = 
							new ButtonsTemplate.ButtonsTemplateBuilder("選擇一個帳戶").build();
					for (Account account : accounts) {
						reply += account.name + ": " + Integer.toString(account.balance)+ '\n';
						buttons.addAction(new PostbackAction(account.name, "action=account&data=" + account.id));
					}
					TextMessage overView = new TextMessage(reply);
					TemplateMessage menu = new TemplateMessage("選擇帳戶", buttons);
					List<Message> messages = new ArrayList<Message>();
					messages.add(overView);
					messages.add(menu);
					sender.sendPush(userId, messages, "");
				}
			}
			if (action.equals("qr")) {
				// JUST DO TEST DATA FOR NOW
				String url = "https://peaceful-plains-74132.herokuapp.com/";
				url += '?';
				url += "target=TEST";
				url += '&';
				url += "amount=12345";
				List<Message> messages = new ArrayList<Message>();
				messages.add(new TextMessage("掃描下面的QR碼付款"));
				messages.add(new ImageMessage(url, url));
				sender.sendPush(userId, messages, "");
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
	
	// Helper method to handle a message to the chatbot API
	private void handleMessage(String nodeId, String message, String token, String userId) {
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
			Util.sendSingleTextPush(sender, userId, responseMessage);
			// Set the next node if it exists
			expectingInput = true;
			JsonArray nodes = response.getAsJsonArray("nodes");
			if (nodes != null && nodes.isJsonArray() && nodes.size() > 0) {
				JsonObject node = nodes.get(0).getAsJsonObject();
				if (node.has("forward") && !node.get("forward").isJsonNull()) {
					nextNodeId = node.get("forward").getAsString();	
				}
				// JUST PARSE THE NODES NO MATTER WHAT FOR NOW
				/*
				List<String> types = Arrays.asList(node.get("nodetype").getAsString().split(","));
				if (!types.contains("QS") && !types.contains("Q")) return;
				*/
			}
		}
		JsonArray nodes = response.getAsJsonArray("nodes");
		if (nodes.size() > 0) {
			handleNodes(nodes, userId);	
		} else {
			sendInitialMessage(userId);
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
		
		System.out.println("> DEBUG: " + ">>> SENDING REQUEST W/ BODY: " + obj.toString());
		
        // request headers
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        if (token != null && !token.equals("")) headers.put("Authorization", "Bearer " + token);
        
        JsonObject response = HttpUtils.sendPost(CHATBOT_API_URL, headers, obj);
        System.out.println("> DEBUG: " + response.toString());
        return response;
	}
	
	// Helper method to send initial menu to user for a list of actions
	private void sendInitialMessage(String userId) {
		JsonObject response = HttpUtils.sendGet(CHATBOT_MENU_URL, null);
		if (response == null) return;
		if (!response.has("nodes") || !response.get("nodes").isJsonArray()) return;
		// Construct the initial menu from the nodes data
		JsonArray nodes = response.getAsJsonArray("nodes");
		ChatbotNodeHandler.constructMenuFromNodes(nodes, userId, sender);
	}
	
}
