package com.pershing.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pershing.message.Message;
import com.pershing.message.TextMessage;
import com.pershing.sender.MessageSender;

/**
 * Utilities class that simplifies certain common tasks
 * 
 * @author ianw3214
 *
 */
public class Util {

	// Send a single reply message to a user
	public final static void sendSingleReply(MessageSender sender, String replyToken, Message message) {
		List<Message> reply = new ArrayList<Message>();
		reply.add(message);
		sender.sendReply(replyToken, reply, "");
	}
	
	// Send a single push message to a user
	public final static void sendSinglePush(MessageSender sender, String userId, Message message) {
		List<Message> push = new ArrayList<Message>();
		push.add(message);
		sender.sendPush(userId, push, "");
	}
	
	// Send a single reply text message to a user
	public final static void sendSingleTextReply(MessageSender sender, String replyToken, String text) {
		List<Message> reply = new ArrayList<Message>();
		reply.add(new TextMessage(text));
		sender.sendReply(replyToken, reply, "");
	}
	
	// Send a single push text message to a user
	public final static void sendSingleTextPush(MessageSender sender, String userId, String text) {
		List<Message> message = new ArrayList<Message>();
		message.add(new TextMessage(text));
		sender.sendPush(userId, message, "");
	}
	
	/**
	 * Parses a query string into a map of key/value pairs
	 * 	e.g. 'A=1&B=2' -> 'A':'1', 'B':'2'
	 * 
	 * @param data	The query string to be parsed
	 * @return		A hashmap of key value pairs represented in the string
	 */
	public final static Map<String, String> getQueryStringAsMap(String data) {
		Map<String, String> result = new HashMap<String, String>();
		List<String> items = Arrays.asList(data.split("&"));
		for (String item : items) {
			String[] split = item.split("=");
			// If there are two items after splitting the string, store as key/value pairs
			if (split.length >= 2) {
				result.put(split[0], split[1]);
			}
			// If there is one item after splitting the string, store it as a key with no value
			if (split.length == 1) {
				result.put(split[0], "");
			}
		}
		return result;
	}
	
}
