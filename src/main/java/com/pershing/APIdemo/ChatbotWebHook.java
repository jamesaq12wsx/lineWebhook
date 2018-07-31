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
	private static final String RICH_MENU_ID = "richmenu-68348ab1daabbd7e87b16806b314d115";
	
	public ChatbotWebHook(String channelSecret, String channelAccessToken) {
		super(channelSecret, channelAccessToken);
	}
	
	@Override
	protected void handleEvent(String userId, WebHookEvent event) {
		// Call the follow event handler if the event is a follow event
		if (event.type() == WebHookEventType.FOLLOW) {
			sendWelcomeMessage(userId);
			messageSender.linkRichMenu(RICH_MENU_ID, userId);
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
			String data = postbackEvent.postbackData();
			Map<String, String> parameters = Util.getQueryStringAsMap(data);
			// Parse the data for specific predefined actions
			if (parameters.containsKey("action")) {
				String action = parameters.get("action");
			}
			// Parse the data for forward nodes to interact with chatbot API
			if (parameters.containsKey("forward")) {
				String forward = parameters.get("forward");
				String paramData = parameters.containsKey("data") ? parameters.get("data") : "";
				// TODO: handle the forward / data as messages?
			}
		}
	}
	
	private final void sendWelcomeMessage(String userId) {
		// First send a get request to chatbot API to get initial menu
		HttpEntity response = HttpUtils.sendGet(CHATBOT_MENU_URL, null);
		if (response == null) {
			log(">>> [ChatbotWebHook] ERROR, initial menu failed to load");
			return;
		}
		try {
			String data = EntityUtils.toString(response);
			JsonParser parser = new JsonParser();
			JsonObject obj = parser.parse(data).getAsJsonObject();
			JsonArray nodes = obj.getAsJsonArray("nodes");
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
			Util.sendSinglePush(messageSender, userId, message);
		} 
		catch (ParseException e) { e.printStackTrace(); }
		catch(IOException e) {e.printStackTrace(); }
	}
	
	private final void handleTextMessage(String message, String userId) {
		// TODO: Handle incoming text messages
	}

}
