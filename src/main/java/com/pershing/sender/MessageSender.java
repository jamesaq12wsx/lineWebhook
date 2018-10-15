package com.pershing.sender;

import java.util.List;

import com.pershing.message.Message;

/**
 * Interface for sending push/reply messages as well as linking/unlinking rich menus
 * 
 * @author ianw3214
 *
 */
public interface MessageSender {

	public Response sendReply(String token, List<Message> replyMessages, String metadata);
	public Response sendPush(String userId, List<Message> pushMessages, String metadata);
	public Response linkRichMenu(String richMenuId, String userId);
	public Response UnlinkRichMenu(String userId);
	
}
