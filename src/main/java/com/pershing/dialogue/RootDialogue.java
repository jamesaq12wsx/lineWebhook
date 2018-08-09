package com.pershing.dialogue;

/**
 * Root dialogue class of a dialogue stack
 * 	- Note that a root dialogue is an extension of a base dialogue, and the only
 * 		difference is that it requires a CREATE method so that the application can
 * 		instantiate a new root dialogue for new users at will.
 * 
 * @author ianw3214
 *
 */
public abstract class RootDialogue extends Dialogue {

	// The required create method for root dialogues
	public abstract RootDialogue create();
	
	// Set the corresponding dialogue pointers for a root dialogue
	public RootDialogue() {
		this.setPrevious(null);
		this.setRoot(this);
		this.setTop(this);
	}
	
	// Factory method to create a demo root dialogue
	public static RootDialogue createDefault() {
		return new EchoRootDialogue();
	}
	
}
