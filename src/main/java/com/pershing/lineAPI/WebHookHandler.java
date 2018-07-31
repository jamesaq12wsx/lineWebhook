package com.pershing.lineAPI;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pershing.dialogue.Dialogue;
import com.pershing.dialogue.DialogueStack;
import com.pershing.dialogue.RootDialogue;
import com.pershing.event.AccountLinkEvent;
import com.pershing.event.BeaconEvent;
import com.pershing.event.FollowEvent;
import com.pershing.event.JoinEvent;
import com.pershing.event.LeaveEvent;
import com.pershing.event.MessageEvent;
import com.pershing.event.PostbackEvent;
import com.pershing.event.UnfollowEvent;
import com.pershing.event.WebHookEvent;
import com.pershing.sender.MessageSender;
import com.pershing.sender.MessageSenderFactory;

/**
 * Webhook handler that uses dialogue stacks to handle logic
 * 
 * @author ianw3214
 *
 */
public class WebHookHandler extends BaseWebHookHandler {
	
	// Map of users to dialogue stacks to keep track of individual states
	private Map<String, DialogueStack> stacks;
	// The root dialogue to instantiate for new users
	private RootDialogue rootDialogue;
	
	/**
	 * Default constructor for a web hook handler
	 * 
	 * @param inChannelSecret	The channel secret of the line bot
	 * @param inChannelAccessToken	The channel access token of the line bot
	 */
	public WebHookHandler(String channelSecret, String channelAccessToken) {
		super(channelSecret, channelAccessToken);
		this.stacks = new HashMap<String, DialogueStack>();
		this.rootDialogue = RootDialogue.createDefault();
		Dialogue.setSender(messageSender);
	}
	
	/**
	 * Constructor where MessageSender is specified and not defaulted to HTTP
	 * 
	 * @param inChannelSecret	The channel secret of the line bot
	 * @param inChannelAccessToken	The channel access token of the line bot
	 * @param sender			The MessageSender object to send push/reply messages
	 */
	public WebHookHandler(String inChannelSecret, String inChannelAccessToken, MessageSender sender) {
		super(inChannelSecret, inChannelAccessToken, sender);
		this.stacks = new HashMap<String, DialogueStack>();
		this.rootDialogue = RootDialogue.createDefault();
		Dialogue.setSender(messageSender);
	}
	
	/**
	 * Setter method for the root dialogue of the webhook handler
	 * @param dialogue
	 */
	public final void setRootDialogue(RootDialogue dialogue) {
		this.rootDialogue = dialogue;
	}
	
	/**
	 * The event handler function that calls the handle event method 
	 * 		of the current dialogue stack of the user
	 */
	@Override
	protected void handleEvent(String userId, WebHookEvent event) {
		// find the users dialogue stack if it exists and handle the message there
		if (!stacks.containsKey(userId)) {
			stacks.put(userId, new DialogueStack(rootDialogue.create()));
		}
		stacks.get(userId).handleEvent(event, userId);
	}
	
}
