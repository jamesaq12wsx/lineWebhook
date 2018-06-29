package com.pershing.sender;

import java.util.List;

import com.pershing.message.Message;

/**
 * Interface for sending push/reply messages
 * 
 * @author ianw3214
 *
 */
public interface MessageSender {

	public Response sendReply(String channelAccessToken, String token, List<Message> replyMessages);
	public Response sendPush(String channelAccessToken, String userId, List<Message> pushMessages);
	
}
