package com.pershing.sender;

/**
 * Factory class for MessageSenders
 * 	- Defaults to directly sending an HTTP request to send push/reply messages directly to LINE
 * @author ianw3214
 *
 */
public class MessageSenderFactory {

	public static MessageSender createDefault() {
		return new HTTPMessageSender();
	}
	
}
