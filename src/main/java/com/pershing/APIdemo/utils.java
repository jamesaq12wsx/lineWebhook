package com.pershing.APIdemo;

import java.util.HashMap;
import java.util.Map;

public class utils {

	public static boolean containsString(String str, String sub) {
		return str.indexOf(sub) != -1;
	}
	
	public static Map<String, String> parseQueryString(String query) {
		// System.out.println("> PARSING QUERY: " + query);
	    Map<String, String> result = new HashMap<String, String>();
	    for (String param : query.split("&")) {
	    	// System.out.println("> PARSING PARAM: " + param);
	        String[] entry = param.split("=");
	        if (entry.length > 1) {
	            result.put(entry[0], entry[1]);
	            // System.out.println("> ENTRY ADDED: " + entry[0] + " -> " + entry[1]);
	        }else{
	            result.put(entry[0], "");
	            // System.out.println("> ENTRY ADDED: " + entry[0] + " -> ()");
	        }
	    }
	    // System.out.println("RETURNING RESULT");
	    return result;
	}
	
}
