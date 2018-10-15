package com.pershing.APIdemo;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * A utility class for sending http requests
 * 
 * @author ianw3214
 *
 */
public class HttpUtils {

	/**
	 * Send a POST request with a JSON object body
	 * 	- fails silently if a HTTP request doesn't succeed
	 * 
	 * @param url		The endpoint to send the request to
	 * @param headers	A map of headers to add to the HTTP request
	 * @param obj		The JSON data to be sent with the request
	 * @return			The JSON data response from the server
	 */
	public static JsonObject sendPost(String url, Map<String, String> headers, JsonObject obj) {	
		// initialize the HTTP request
		HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(url);
        
    	// request headers
        if (headers != null) {
            for (String key : headers.keySet()) {
            	httppost.setHeader(key, headers.get(key));
            }	
        }
        
        if (obj != null) {
        	StringEntity params = new StringEntity(obj.toString(), "UTF-8");
        	httppost.setEntity(params);
        }
		
        // execute and get the response
        HttpResponse response = null;
		try {
			response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
		// handle the server response
        HttpEntity entity = response.getEntity();
        JsonObject result = null;
        if (entity != null) {
        	// verify that the status code is what we want
        	int status = response.getStatusLine().getStatusCode();
        	if (status != 200) return null;
        	try {
        		String data = EntityUtils.toString(entity);
        		JsonParser parser = new JsonParser();
        		result = parser.parse(data).getAsJsonObject();
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        }
        httppost.releaseConnection();
    	return result;
	}
	
	/**
	 * Send a GET request
	 * 	- fails silently if a HTTP request doesn't succeed
	 * 
	 * @param url		The endpoint to send the request to
	 * @param headers	A map of headers to add to the HTTP request
	 * @return			The JSON data response from the server
	 */
	public static JsonObject sendGet(String url, Map<String, String> headers) {
		// initialize the HTTP request
		HttpClient httpClient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet(url);
		
		// request headers
		if (headers != null) {
			for (String key : headers.keySet()) {
				httpget.setHeader(key, headers.get(key));
			}	
		}
		
		// execute and get the response
		HttpResponse response = null;
		try {
			response = httpClient.execute(httpget);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// handle the server response
        HttpEntity entity = response.getEntity();
        JsonObject result = null;
        if (entity != null) {
        	// verify that the status code is what we want
        	int status = response.getStatusLine().getStatusCode();
        	if (status != 200) return null;
        	try {
        		String data = EntityUtils.toString(entity);
        		JsonParser parser = new JsonParser();
        		result = parser.parse(data).getAsJsonObject();
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        }
        httpget.releaseConnection();
    	return result;
	}
	
}
