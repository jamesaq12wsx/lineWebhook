package com.pershing.APIdemo;

import java.util.ArrayList;
import java.util.List;

/**
 * Data class to hold user information
 * 
 * @author ianw3214
 *
 */
public class UserData {

	// flag to see whether the user is valid yet or not
	public boolean valid;
	public String code;
	
	// user properties
	public int balance;
	public String phone;
	
	// payment options
	public List<String> payees;
	
	public UserData() {
		this.valid = false;
		this.code = "";
		this.balance = 0;
		this.phone = "";
		this.payees = new ArrayList<String>();
	}
	
}
