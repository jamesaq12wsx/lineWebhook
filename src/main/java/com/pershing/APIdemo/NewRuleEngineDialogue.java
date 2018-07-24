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

public class NewRuleEngineDialogue extends RootDialogue {


	private static final String richMenuId = "richmenu-9a514e3da2598a348836d6460b1fc5e1";
	
	// store the nodes to avoid constantly calling on them
	private JsonObject nodeTreeJson;
	
	@Override
	public RootDialogue create() {
		return new RuleEngineDialogue();
	}

	@Override
	public void handleEvent(WebHookEvent event, String userId) {
		if (event.type() == WebHookEventType.MESSAGE) {
			MessageEvent messageEvent = (MessageEvent) event;
			if (messageEvent.message().type() == MessageType.TEXT) {
				TextMessage textMessage = (TextMessage) messageEvent.message();
				// temporarily override message parsing for LIFF app testing
				if (textMessage.getText().toLowerCase().contains("test")) {
					ButtonsTemplate buttons = new ButtonsTemplate.ButtonsTemplateBuilder("TEST").build();
					buttons.addAction(new URIAction("test", "https://line.me/R/app/1588952156-kX2KV06z"));
					TemplateMessage message = new TemplateMessage("TEST", buttons);
					Util.sendSinglePush(sender, userId, message);
				}
				JsonObject response = ruleEngineRequest(textMessage.getText(), userId);
				if (response == null) {
					Util.sendSingleTextReply(sender, userId, "Sorry, message could not be understood.");
				} else {
					String type = response.get("type").getAsString();
					if (type.equals("buttons")) {
						handleButtonResponse(response, userId);
					}
					if (type.equals("error")) {
						
					}
					if (type.equals("text")) {
						
					}
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

	private void handleButtonResponse(JsonObject response, String userId) {
		JsonArray buttons = response.getAsJsonArray("content");
		ButtonsTemplate bt = new ButtonsTemplate.ButtonsTemplateBuilder(
				response.get("text").getAsString()).build();
		for (JsonElement e : buttons) {
			JsonObject button = e.getAsJsonObject();
			String buttonType = button.get("type").getAsString();
			if (buttonType.equals("message")) {
				
			}
			bt.addAction(new PostbackAction(button.get("text").getAsString(), 
					"forward=" + button.get("data").getAsString(), button.get("title").getAsString()));
		}
		TemplateMessage message = new TemplateMessage(response.get("nodetitle").getAsString(), bt);
		Util.sendSinglePush(sender, userId, message);
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
			System.out.println(">>> POSTBACK DATA FORWARD: " + forward);
			Util.sendSingleTextPush(sender, userId, "FOrWARD TO: " + forward);
		}
	}
	
	// Helper method to get the next node depending on the sent message from backend API
	private JsonObject ruleEngineRequest(String message, String userId) {
		// construct the json object to be sent first
		JsonObject obj = new JsonObject();
		obj.addProperty("channel", "line");
		obj.addProperty("messagetype", "text");
		obj.addProperty("message", message);
		obj.addProperty("userid", userId);
		
		// initialize the HTTP request
		HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("https://lit-tor-78027.herokuapp.com/");
        
        // request headers
        httppost.setHeader("Content-Type", "application/json");
        
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
		// send a GET request to get all the nodes
		// initialize the HTTP request
		HttpClient httpclient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet("https://lit-tor-78027.herokuapp.com/");
        
        // execute and get the response
        HttpResponse response = null;
		try {
			response = httpclient.execute(httpget);
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
        	String message = response.getStatusLine().toString();
        	if (status != 200) {
        		System.out.println("Something went wrong, message: " + message);
        		return;
        	}
    		try {
    			// Assume the response is always encoded in UTF-8 
				String data = EntityUtils.toString(entity, "UTF-8");
				JsonParser parser = new JsonParser();
				nodeTreeJson = parser.parse(data).getAsJsonObject();
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}	
        } else {
            // release the connection when finished
            httpget.releaseConnection();
        }
        
        ButtonsTemplate buttons = new ButtonsTemplate.ButtonsTemplateBuilder(
        		"Welcome, pick an option below to get started").build();
        // parse the data to construct a message to send
        if (nodeTreeJson != null) {
        	try {
        		JsonArray nodes = nodeTreeJson.getAsJsonArray("nodes");
        		for (JsonElement e : nodes) {
        			JsonObject node = e.getAsJsonObject();
        			String nodeType = node.get("nodetype").getAsString();
        			if (nodeType.equals("D")) {
        				String text = node.get("nodetitle").getAsString();
        				buttons.addAction(new MessageAction(text, text));
        			}
        		}
        	} catch (Exception e) {
        		e.printStackTrace();
        		return;
        	}
        }
        TemplateMessage message = 
        		new TemplateMessage("Welcome, pick an option below to get started", buttons);
        Util.sendSinglePush(sender, userId, message);
	}

}