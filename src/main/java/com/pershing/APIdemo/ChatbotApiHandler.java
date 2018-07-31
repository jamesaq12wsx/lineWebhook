package com.pershing.APIdemo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ChatbotApiHandler implements HttpHandler {

	private static final String CHANNEL_SECRET = "39a7a8b5594e8fe81bac12e31890752e";
	private static final String CHANNEL_ACCESS_TOKEN = "B+R5Dl03JENfxXW+ALdsS6yHlFvBlB1xIrMh1ReV7qwgxnKflMyV+ludAmyhCq3oLt6XIzXQlv/vSUXSUBFNRbc+HP2TYfLZv5oMCQHGq+xsrZpDmRd+nj+KeJWrVtkYbD8r/uxDPXRUS21iE15iQgdB04t89/1O/w1cDnyilFU=";
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String method = exchange.getRequestMethod();
		boolean success = false;
		if (method.equals("POST")) {
			String body = getBody(exchange);
			String header = getLineVerificationHeader(exchange);
			// HANDLE THE THING HERE
		}
		
		if (success) {
			String response = "HTTP request processed successfully";
			exchange.sendResponseHeaders(200, response.length());
			OutputStream os = exchange.getResponseBody();
			os.write(response.getBytes());
			os.close();
		} else {
			String response = "HTTP request failed";
			exchange.sendResponseHeaders(500, response.length());
			OutputStream os = exchange.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}
	
	/**
	 * Extracts the body of the request
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
	 */
	private String getLineVerificationHeader(HttpExchange exchange) {
		
		return exchange.getRequestHeaders().get("X-Line-Signature").toString();
		
	}

	
}
