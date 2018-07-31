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

import com.google.gson.JsonObject;

public class HttpUtils {

	// Send a post request with a Json object body
	public static HttpEntity sendPost(String url, Map<String, String> headers, JsonObject obj) {	
		// initialize the HTTP request
		HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(url);
        
        if (headers != null) {
        	// request headers
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
        httppost.releaseConnection();
        return entity;
	}
	
	// Send a get request
	public static HttpEntity sendGet(String url, Map<String, String> headers) {
		// initialize the HTTP request
		HttpClient httpClient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet(url);
		
		if (headers != null) {
			// request headers
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
		httpget.releaseConnection();
		return entity;
	}
	
}
