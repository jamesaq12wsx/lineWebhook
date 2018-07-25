package com.pershing.APIdemo;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pershing.action.MessageAction;
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

	private static final String richMenuId = "richmenu-76285471ee4ecd698547d0b0087ca22a";
	
	private boolean expectingInput;
	private String nextNodeId;
	private String currentToken;
	
	public RuleEngineDialogue() {
		expectingInput = false;
		nextNodeId = "";
		currentToken = "";
	}
	
	@Override
	public RootDialogue create() {
		return new RuleEngineDialogue();
	}

	@Override
	public void handleEvent(WebHookEvent event, String userId) {
		if (event.type() == WebHookEventType.MESSAGE) {
			MessageEvent messageEvent = (MessageEvent) event;
			if (messageEvent.message().type() == MessageType.TEXT) {
				if (expectingInput) {
					TextMessage textMessage = (TextMessage) messageEvent.message();
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
				String type = node.get("nodetype").getAsString();
				if (type.equals("D") || type.equals("DD")) {
					// print a menu with the next nodes as options
					ButtonsTemplate buttons = new ButtonsTemplate.ButtonsTemplateBuilder(
							node.get("nodetitle").getAsString()).build();
					buttons.addAction(new PostbackAction(
							node.get("content").getAsString(),
							"forward=" + node.get("forward").getAsString()));
					TemplateMessage message = new TemplateMessage(node.get("content").getAsString(), buttons);
					Util.sendSinglePush(sender, userId, message);
				}
				if (type.equals("B")) {
					// print a menu with the specfied buttons
					ButtonsTemplate buttons = new ButtonsTemplate.ButtonsTemplateBuilder(
							node.get("nodetitle").getAsString()).build();
					JsonArray content = node.getAsJsonArray("content");
					for (JsonElement button : content) {
						JsonObject buttonObject = button.getAsJsonObject();
						buttons.addAction(new PostbackAction(
								buttonObject.get("title").getAsString(), 
								"forward=" + buttonObject.get("forward").getAsString()));
					}
					TemplateMessage message = new TemplateMessage(node.get("nodetitle").getAsString(), buttons);
					Util.sendSinglePush(sender, userId, message);
				}
				if (type.equals("L")) {
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
		if (data.substring(0, 7).equals("action=")) {
			String action = data.substring(7);
			System.out.println(">>> POSTBACK DATA ACTION: " + action);
			if (action.equals("balance")) {
				Util.sendSingleTextPush(sender, userId, "BALANCE HERE");
			}
			if (action.equals("interest")) {
				Util.sendSingleTextPush(sender, userId, "INTEREST RATES HERE");
			}
			if (action.equals("exchange")) {
				Util.sendSingleTextPush(sender, userId, "EXCHANGE RATES HERE");
			}
			if (action.equals("smart_transfer")) {
				Util.sendSingleTextPush(sender, userId, "SMART TRANSFER HERE");
			}
			if (action.equals("qr_transfer")) {
				Util.sendSingleTextPush(sender, userId, "QR TRANSFER HERE");
			}
			if (action.equals("service")) {
				Util.sendSingleTextPush(sender, userId, "SERVICE POINTS HERE");
			}
		}
		// parse the data as a forward action trigger if the data specifies it
		if (data.substring(0, 8).equals("forward=")) {
			String forward = data.substring(8); 
			System.out.println(">>> POSTBACK FORWARD: " + forward);
			handleMessage(forward, "", "", userId);
		}
	}
	
	private void handleMessage(String nodeId, String message, String token, String userId) {
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
						if (nodes.size() > 0) {
							JsonObject node = nodes.get(0).getAsJsonObject();
							nextNodeId = node.get("forward").getAsString();
						}
						return;
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
		
		// initialize the HTTP request
		HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("https://chatbotapipsc.azurewebsites.net/api/chatbot/");
        
        // request headers
        httppost.setHeader("Content-Type", "application/json");
        if (token != null && !token.equals("")) {
        	httppost.setHeader("Authorization", "Bearer " + token);
        }
        
        StringEntity params = new StringEntity(obj.toString(), "UTF-8");
		
		httppost.setEntity(params);
        // execute and get the response
        HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
		// handle the server response
        HttpEntity entity = response.getEntity();
        if (entity != null) {
    		// verify that the status code is what we want
        	int status = response.getStatusLine().getStatusCode();
        	if (status != 200) return null;
    		try {
				String data = EntityUtils.toString(entity);
				JsonParser parser = new JsonParser();
				System.out.println(">>> RULE ENGINE RESPONSE: " + parser.parse(data).getAsJsonObject().toString());
				return parser.parse(data).getAsJsonObject();
			} 
    		catch (ParseException e) { e.printStackTrace(); } 
    		catch (IOException e) { e.printStackTrace(); }	
        } else {
            // release the connection when finished
            httppost.releaseConnection();
            return null;
        }
		
        // The code should not run to this point
		return null;
	}
	
	// Helper method to send initial menu to user for a list of actions
	private void sendInitialMessage(String userId) {
		JsonObject response = ruleEngineRequest("", "", "", userId);
		// We know the response contains all the default nodes, no need to validate
		if (response == null) {
			Util.sendSingleTextPush(sender, userId, "Sorry, message could not be understood.");
		} else {
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
								"forward=" + node.get("nodeid").getAsString()));
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
