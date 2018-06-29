package com.pershing.sender;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

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
import com.google.gson.JsonObject;
import com.pershing.message.Message;

/**
 * Message sender implementation that directly sends the messages to LINE
 * 
 * TODO:
 * 	- Use pooled http connection manager or something to promote more efficient use of resources
 * 
 * @author ianw3214
 *
 */
public class HTTPMessageSender implements MessageSender {

	/**
	 * Send a reply to an event directly to LINE via HTTPS
	 * 
	 * @param channelAccessToken	The channel access token of the bot
	 * @param token					The reply Token of the event
	 * @param replyMessages			The reply messages to be sent to the source
	 */
	public Response sendReply(String channelAccessToken, String token, List<Message> replyMessages) {
		
		// initialize the HTTP request
		HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("https://api.line.me/v2/bot/message/reply");
        
        // request headers
        httppost.setHeader("Content-Type", "application/json");
        httppost.setHeader("Authorization", "Bearer " + channelAccessToken);
        
        // setup request parameters and other properties
        JsonArray messages = new JsonArray();
        for (Message m : replyMessages) {
        	messages.add(m.getAsJsonObject());
        }
        JsonObject body = new JsonObject();
        body.addProperty("replyToken", token);
        body.add("messages", messages);
        
        StringEntity params = null;
		try {
			params = new StringEntity(body.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	        // release the connection when finished
	        httppost.releaseConnection();
			return Response.constructEmptyResponse();
		}
		
		httppost.setEntity(params);
        // execute and get the response
        HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int status = -1;
		String message = "";
		String data = "";
		
		// handle the server response
        HttpEntity entity = response.getEntity();
        if (entity != null) {
    		// verify that the status code is what we want
        	status = response.getStatusLine().getStatusCode();
        	message = response.getStatusLine().toString();
    		try {
				data = EntityUtils.toString(entity);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
        } else {
            // release the connection when finished
            httppost.releaseConnection();
            return Response.constructEmptyResponse();
        }	
        
		return new Response(status, message, data);
	}

	/**
	 * Send a push message directly to LINE via HTTPS
	 * 
	 * @param channelAccessToken	The channel access token of the bot
	 * @param userId				The LINE messenger ID of the target user
	 * @param pushMessages			The push messages to be sent to the user
	 */
	public Response sendPush(String channelAccessToken, String userId, List<Message> pushMessages) {
		
		// initialize the HTTP request
		HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("https://api.line.me/v2/bot/message/push");
        
        // request headers
        httppost.setHeader("Content-Type", "application/json");
        httppost.setHeader("Authorization", "Bearer " + channelAccessToken);
        
        // setup request parameters and other properties
        JsonArray messages = new JsonArray();
        for (Message m : pushMessages) {
        	messages.add(m.getAsJsonObject());
        }
        JsonObject body = new JsonObject();
        body.addProperty("to", userId);
        body.add("messages", messages);
        
        StringEntity params = null;
		try {
			params = new StringEntity(body.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	        // release the connection when finished
	        httppost.releaseConnection();
			return Response.constructEmptyResponse();
		}
		
		httppost.setEntity(params);
        // execute and get the response
        HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int status = -1;
		String message = "";
		String data = "";
		
		// handle the server response
        HttpEntity entity = response.getEntity();
        if (entity != null) {
    		// verify that the status code is what we want
        	status = response.getStatusLine().getStatusCode();
        	message = response.getStatusLine().toString();
    		try {
				data = EntityUtils.toString(entity);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
        } else {
            // release the connection when finished
            httppost.releaseConnection();
            return Response.constructEmptyResponse();
        }	
        
		return new Response(status, message, data);
	}

}
