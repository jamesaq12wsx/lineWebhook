package com.pershing.event;

/**
 * Enumeration of all webhook event types of the LINE Messaging API
 * 	- See documentation @ https://developers.line.me/en/docs/messaging-api/reference/#webhooks
 * 
 * @author ianw3214
 *
 */
public enum WebHookEventType {
	MESSAGE, FOLLOW, UNFOLLOW, JOIN, LEAVE, POSTBACK, BEACON, ACCOUNT_LINK
}
