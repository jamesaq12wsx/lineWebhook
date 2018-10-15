package com.pershing.mockAPI;

import java.util.ArrayList;
import java.util.List;

public class Account {

	public int balance;
	public String name;
	public String id;
	public List<String> history;
	
	public Account() {
		balance = 0;
		name = "";
		id = "";
		history = new ArrayList<String>();
	}
	
	public Account(int balance, String name, String id) {
		this.balance = balance;
		this.name = name;
		this.id = id;
		this.history = new ArrayList<String>();
	}
	
}
