package com.pershing.lineAPI;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.pershing.dialogue.Dialogue;
import com.pershing.dialogue.DialogueStack;
import com.pershing.dialogue.RootDialogue;
import com.pershing.event.WebHookEvent;
import com.pershing.sender.MessageSender;

/**
 * Webhook handler that uses dialogue stacks to handle logic
 * 	- See the documentation for more details about the API
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
	
	/**
	 * Getter method for the list of users in the webhook handler
	 * @return		The list of users in the webhook handler
	 */
	public final Set<String> getUsers() {
		return stacks.keySet();
	}
	
}
