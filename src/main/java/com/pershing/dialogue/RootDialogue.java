package com.pershing.dialogue;

public abstract class RootDialogue extends Dialogue {

	public abstract RootDialogue create();
	
	// Set the corresponding dialogue pointers for a root dialogue
	public RootDialogue() {
		this.setPrevious(null);
		this.setRoot(this);
		this.setTop(this);
	}
	
	public static RootDialogue createDefault() {
		return new EchoRootDialogue();
	}
	
}
