package com.pershing.APIdemo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pershing.action.MessageAction;
import com.pershing.dialogue.RootDialogue;
import com.pershing.event.FollowEvent;
import com.pershing.event.MessageEvent;
import com.pershing.event.WebHookEvent;
import com.pershing.event.WebHookEventType;
import com.pershing.message.Message;
import com.pershing.message.MessageType;
import com.pershing.message.TemplateMessage;
import com.pershing.message.TextMessage;
import com.pershing.sender.Response;
import com.pershing.template.ButtonsTemplate;
import com.pershing.util.Util;

public class RuleEngineDialogue extends RootDialogue {

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
				JsonObject response = ruleEngineRequest(textMessage.getText(), userId);
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
		if (event.type() == WebHookEventType.FOLLOW) {
			sendInitialMessage(userId);
		}
	}
	
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
							buttons.addAction(new MessageAction(title, title));	
						}
					}
					TemplateMessage message = new TemplateMessage(node.get("content").getAsString(), buttons);
					Util.sendSinglePush(sender, userId, message);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
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
	
	private void sendInitialMessage(String userId) {
		// send a GET request to get all the nodes
		// initialize the HTTP request
		HttpClient httpclient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet("https://chatbotapipsc.azurewebsites.net/api/chatbot/");
        
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

	private JsonObject findNodeViaId(String id) {
		try {
			JsonArray nodes = nodeTreeJson.getAsJsonArray("nodes");
			for (JsonElement e : nodes) {
				JsonObject obj = e.getAsJsonObject();
				if (obj.get("nodeid").equals(id)) {
					return obj;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
