package com.pershing.mockAPI;

import java.util.ArrayList;
import java.util.List;

public class Payee {

	String name;
	List<String> history;
	
	public Payee() {
		name = "";
		history = new ArrayList<String>();
	}
	
	public Payee(String name) {
		this.name = name;
		this.history = new ArrayList<String>();
	}
	
}
