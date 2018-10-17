package com.pershing.APIdemo;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import com.pershing.lineAPI.WebHookHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * The root handler that catches all incoming HTTP requests to a server and handles them
 * 
 * @author ianw3214
 *
 */
public class RootHandler implements HttpHandler {
	
	// The channel secret of the LINE bot
	private static final String CHANNEL_SECRET = "ad4c0413ff6e5fea0b68f8a99b3aeaaa";
	// The channel access token of the LINE bot
	private static final String CHANNEL_ACCESS_TOKEN = "GQY0ZAOLwvi8aq7F4/CDMdhakkdgB4tnVvcfJCFWyQYV+3VDVxYr09ivAz8hRzIIMUFIbRl0+6I5S5bgwZsBOcujqnc1hVD3XsqOYrpBNJYDH9l6LbId/6/iL39hcQwj4jthD9OCZbh+PB+sYmLVpwdB04t89/1O/w1cDnyilFU=";
	
	// The webhookHandler that the line bot runs on
	private WebHookHandler webHookHandler;
	
	/**
	 * Default constructor which instantiates a new webhook handler for the line bot
	 */
	public RootHandler() {
		webHookHandler = new WebHookHandler(CHANNEL_SECRET, CHANNEL_ACCESS_TOKEN);
		webHookHandler.setRootDialogue(new RuleEngineDialogue());
		webHookHandler.setVerbose(true);
	}

	/**
	 * Handler method for incoming HTTP requests
	 */
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		// call the POST request handler if the request is a POST request
		String method = exchange.getRequestMethod();
		boolean success = false;
		if (method.equals("POST")) {
			// Call the webhook event handler if a POST event was recieved
			System.out.println(">>> [ROOT HANDLER] Recieved POST request");
			String body = getBody(exchange);
			String header = getHeader(exchange);
			success = webHookHandler.handleWebHookEvent(header, body);
		}
		if (method.equals("GET")) {
			System.out.println(">>> [ROOT HANDLER] recieved GET Request");
			// For now, assume all GET requests just want a QR code image
			success = QRCodeGenerator.handleQRCodeFromGet(exchange);
			// if the QR code generation was successful, then a response was already sent
			if (success) return;
		}
		
		// Return a response indicating whether something went wrong or not
		if (success) {
			System.out.println(">>> [ROOT HANDLER] HTTP request processed successfully");
			String response = "HTTP request processed successfully";
			exchange.sendResponseHeaders(200, response.length());
			OutputStream os = exchange.getResponseBody();
			os.write(response.getBytes());
			os.close();	
		} else {
			System.out.println(">>> [ROOT HANDLER] HTTP request failed");
			String response = "HTTP request failed";
			exchange.sendResponseHeaders(500, response.length());
			OutputStream os = exchange.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}
	
	/**
	 * Extracts the body of the request
	 * 
	 * @param exchange	The HttpExchange variable
	 * @return	The body of the request
	 */
	private String getBody(HttpExchange exchange) {
		
		InputStream stream = exchange.getRequestBody();
		Scanner scanner = new Scanner(stream, "UTF-8");
		String str = scanner.hasNext() ? scanner.nextLine() : "";
		scanner.close();
		
		return str;
	}
	
	/**
	 * Extracts the header (X-line-signature) of the request
	 * 
	 * @param exchange The HttpExchange variable
	 * @return	The header of the request
	 */
	private String getHeader(HttpExchange exchange) {
		
		return exchange.getRequestHeaders().get("X-Line-Signature").toString();
		
	}

	/**
	 * Getter method for the webhookHandler of the RootHandler
	 */
	public WebHookHandler getWebHookHandler() {
		return webHookHandler;
	}
	
}
