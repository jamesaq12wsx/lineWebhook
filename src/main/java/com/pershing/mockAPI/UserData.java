package com.pershing.mockAPI;

import java.util.ArrayList;
import java.util.List;

public class UserData {

	// This shouldn't technically be part of the API but putting it here for demo anyways
	public boolean valid;
	public String code;
	
	public String phone;
	public String email;
	
	public List<Account> accounts;
	public List<Payee> payees;
	
	// TODO: implement favorite actions
	
	public UserData() {
		valid = false;
		code = "";
		
		phone = "";
		email = "";
		
		accounts = new ArrayList<Account>();
		payees = new ArrayList<Payee>();
	}
	
}
