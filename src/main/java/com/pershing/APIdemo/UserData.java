package com.pershing.APIdemo;

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
	
	public int balance;
	public String phone;
	
	public UserData() {
		this.valid = false;
		this.code = "";
		this.balance = 0;
		this.phone = "";
	}
	
}
