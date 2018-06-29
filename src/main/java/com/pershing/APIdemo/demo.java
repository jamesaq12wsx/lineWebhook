package com.pershing.APIdemo;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

public class demo {

	public static void main(String[] args) throws NumberFormatException, IOException {

    	// get the port the server should run on if it exists
    	String port = System.getenv("PORT");
		if (port == null || port.isEmpty()) {
			port = "8080";
		}
		
		// create the HTTP server
		HttpServer server = HttpServer.create(new InetSocketAddress(Integer.valueOf(port)), 0);
		System.out.println("Server started at port: " + port);
		server.createContext("/", new RootHandler());

		// run the HTTP server
		server.setExecutor(null);
		server.start();

		
	}

}
