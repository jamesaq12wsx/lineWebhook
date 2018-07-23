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
import com.pershing.template.CarouselTemplate;
import com.pershing.template.Column;
import com.pershing.util.Util;

public class RuleEngineDialogue extends RootDialogue {

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
					Util.sendSingleTextPush(sender, userId, "Sorry, message could not be understood.");
				} else {
					try {
						JsonArray nodes = response.getAsJsonArray("nodes");
						if (nodes.size() > 0) {
							handleNodes(nodes, userId);	
						} else {
							Util.sendSingleTextReply(sender, messageEvent.replyToken(), "Sorry, message not understood");
						}
					} catch (Exception e) {
						e.printStackTrace();
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
	
	// Helper method to interact with the user based on the input nodes
	private void handleNodes(JsonArray nodes, String userId) {
		try {
			for (JsonElement e : nodes) {
				JsonObject node = e.getAsJsonObject();
				String type = node.get("nodetype").getAsString();
				if (type.equals("D")) {
					// print a menu with the next nodes as options
					ButtonsTemplate buttons = new ButtonsTemplate.ButtonsTemplateBuilder(
							node.get("content").getAsString()).build();
					JsonArray nextNodes = node.getAsJsonArray("nextnode");
					for (JsonElement nextNode : nextNodes) {
						String nodeId = nextNode.getAsString();
						JsonObject nodeObject = findNodeViaId(nodeId);
						if (nodeObject != null) {
							String title = nodeObject.get("nodetitle").getAsString();
							buttons.addAction(new MessageAction(title, nodeId));
						}
					}
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
								"forward=" + buttonObject.get("forward").getAsString(),
								buttonObject.get("title").getAsString()));
					}
					TemplateMessage message = new TemplateMessage(node.get("nodetitle").getAsString(), buttons);
					Util.sendSinglePush(sender, userId, message);
				}
				if (type.equals("L")) {
					// print a menu with the specfied buttons
					ButtonsTemplate buttons = new ButtonsTemplate.ButtonsTemplateBuilder(
							node.get("nodetitle").getAsString()).build();
					try {
						JsonArray content = node.getAsJsonArray("content");
						for (JsonElement button : content) {
							JsonObject buttonObject = button.getAsJsonObject();
							buttons.addAction(new URIAction(
									buttonObject.get("title").getAsString(), 
									buttonObject.get("url").getAsString()));
						}
					} catch (Exception ex) {
						// THIS IS NOT AN ERROR, SOMETIMES LINKS ARE ONLY A SINGLE LINK
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
				JsonObject balanceNode = findNodeViaKeyword("AccountServiceList");
				JsonArray arr = new JsonArray();
				arr.add(balanceNode);
				handleNodes(arr, userId);
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
			JsonObject node = findNodeViaKeyword(forward);
			if (node != null) {
				JsonArray arr = new JsonArray();
				arr.add(node);
				handleNodes(arr, userId);
			} else {
				Util.sendSingleTextPush(sender, userId, "Sorry, something went wrong...");
			}
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
        HttpPost httppost = new HttpPost("https://chatbotapipsc.azurewebsites.net/api/chatbot/");
        
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
		JsonObject response = ruleEngineRequest("", userId);
		// We know the response contains all the default nodes, no need to validate
		if (response == null) {
			Util.sendSingleTextPush(sender, userId, "Sorry, message could not be understood.");
		} else {
			try {
				JsonArray nodes = response.getAsJsonArray("nodes");
				// print a menu with the specfied buttons
				CarouselTemplate carousel = new CarouselTemplate();
				Column currentColumn = new Column("MENU");
				int counter = 0;
				for (JsonElement e : nodes) {
					JsonObject node = e.getAsJsonObject();
					System.out.println(node.toString());
					currentColumn.addAction(new PostbackAction(
							node.get("nodetitle").getAsString(), 
							"forward=" + node.get("forward").getAsString(),
							node.get("nodetitle").getAsString()));
					counter++;
					if (counter % 3 == 0 && counter != 0) {	
						carousel.addColumn(currentColumn);
						currentColumn = new Column("MENU");
					}
				}
				if (currentColumn.numActions() != 0) {
					carousel.addColumn(currentColumn);
				}
				TemplateMessage message = new TemplateMessage("Menu", carousel);
				Util.sendSinglePush(sender, userId, message);
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}
	}

	// Helper method to find a node in the nodetree via the node id
	private JsonObject findNodeViaId(String id) {
		try {
			JsonArray nodes = nodeTreeJson.getAsJsonArray("nodes");
			for (JsonElement e : nodes) {
				JsonObject obj = e.getAsJsonObject();
				if (obj.get("nodeid").getAsString().equals(id)) {
					return obj;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// Helper method to find a node in the nodetree via the node keyword
	private JsonObject findNodeViaKeyword(String keyword) {
		try {
			JsonArray nodes = nodeTreeJson.getAsJsonArray("nodes");
			for (JsonElement e : nodes) {
				JsonObject obj = e.getAsJsonObject();
				if (obj.get("keyword").getAsString().equals(keyword)) {
					return obj;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
