package com.pershing.mockAPI;

import java.util.ArrayList;
import java.util.List;

public class Payee {

	public String name;
	public List<String> history;
	
	public Payee() {
		name = "";
		history = new ArrayList<String>();
	}
	
	public Payee(String name) {
		this.name = name;
		this.history = new ArrayList<String>();
	}
	
}
