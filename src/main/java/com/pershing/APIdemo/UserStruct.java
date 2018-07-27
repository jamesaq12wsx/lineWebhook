package com.pershing.APIdemo;

public class UserStruct {

	public boolean expectingInput;
	public String nextNodeId;
	public String currentToken;
	
	public UserStruct() {
		this.expectingInput = false;
		this.nextNodeId = "";
		this.currentToken = "";
	}
	
}
