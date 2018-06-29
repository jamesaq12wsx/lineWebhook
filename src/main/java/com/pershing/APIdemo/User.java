package com.pershing.APIdemo;

public class User {

	// The userId used by LINE
	public String lineId;
	public String userId;
	public int balance;
	public boolean setup;
	public ExpectedInput currentExpectedInput;
	
	public String currentTransferTArget;
	public int transferAmount;
	
	public User() {
		lineId = "";
		userId = "";
		balance = 0;
		setup = false;
		currentExpectedInput = ExpectedInput.NONE;
		currentTransferTArget = "";
		transferAmount = 0;
	}
	
	public User(String lineId) {
		this.lineId = lineId;
		userId = "";
		balance = 0;
		setup = false;
		currentExpectedInput = ExpectedInput.NONE;
		currentTransferTArget = "";
		transferAmount = 0;
	}
	
	public User(String lineId, int bal) {
		this.lineId = lineId;
		userId = "";
		balance = bal;
		setup = false;
		currentExpectedInput = ExpectedInput.NONE;
		currentTransferTArget = "";
		transferAmount = 0;
	}
	
}
