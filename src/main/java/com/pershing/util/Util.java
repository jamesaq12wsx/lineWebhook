package com.pershing.util;

import java.util.ArrayList;
import java.util.List;

import com.pershing.message.Message;
import com.pershing.message.TextMessage;
import com.pershing.sender.MessageSender;

public class Util {

	public final static void sendSingleReply(MessageSender sender, String replyToken, Message message) {
		List<Message> reply = new ArrayList<Message>();
		reply.add(message);
		sender.sendReply(replyToken, reply, "");
	}
	
	public final static void sendSinglePush(MessageSender sender, String userId, Message message) {
		List<Message> push = new ArrayList<Message>();
		push.add(message);
		sender.sendPush(userId, push, "");
	}
	
	public final static void sendSingleTextReply(MessageSender sender, String replyToken, String text) {
		List<Message> reply = new ArrayList<Message>();
		reply.add(new TextMessage(text));
		sender.sendReply(replyToken, reply, "");
	}
	
	public final static void sendSingleTextPush(MessageSender sender, String userId, String text) {
		List<Message> message = new ArrayList<Message>();
		message.add(new TextMessage(text));
		sender.sendPush(userId, message, "");
	}
	
}
