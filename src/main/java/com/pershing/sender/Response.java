package com.pershing.sender;

/**
 * Data class containing the returned status from sending a request to LINE
 * 
 * @author ianw3214
 *
 */
public class Response {

	// The returned HTTPS status code
	private final int status;
	// The HTTPS response message
	private final String message;
	// The returned data (if any)
	private final String data;
	
	/**
	 * Main constructor which sets all 3 properties of the class
	 * 
	 * @param status
	 * @param message
	 * @param data
	 */
	public Response(int status, String message, String data) {
		this.status = status;
		this.message = message;
		this.data = data;
	}
	
	// GETTER METHODS
	public int status() {
		return status;
	}
	public String message() {
		return message;
	}
	public String data() {
		return data;
	}
	
	/**
	 * Builder function that returns an empty Response Object
	 * 
	 * @return	An empty response object
	 */
	public static final Response constructEmptyResponse() {
		return new Response(0, "", "");
	}
	
}
