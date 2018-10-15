package com.pershing.APIdemo;

import java.io.IOException;
import java.io.OutputStream;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.pershing.lineAPI.WebHookHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class UserHandler implements HttpHandler {

	// Reference to webhook handler to get userIds from
	private WebHookHandler handlerReference;
	
	public UserHandler(WebHookHandler reference) {
		this.handlerReference = reference;
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String method = exchange.getRequestMethod();
		if (method.equals("POST")) {
			sendResponse(exchange, "POST not supported", 400);
		}
		if (method.equals("GET")) {
			JsonObject response = new JsonObject();
			JsonArray users = new JsonArray();
			for (String userId : handlerReference.getUsers()) {
				users.add(userId);
			}
			response.add("users", users);
			sendResponse(exchange, response.toString(), 200);
		}
	}
	
	/**
	 * Send an HTTP response to a request
	 * 
	 * @param exchange	The exchange of the HTTP request
	 * @param message	The message to return in the response
	 * @param status	The status code to return in the response
	 */
	private void sendResponse(HttpExchange exchange, String message, int status) {
		try {
			exchange.sendResponseHeaders(status, message.length());
			OutputStream os = exchange.getResponseBody();
			os.write(message.getBytes());
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
