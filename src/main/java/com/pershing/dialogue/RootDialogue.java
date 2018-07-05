package com.pershing.dialogue;

/**
 * Require Root dialogues to have a factory function to create copies for new users
 * 
 * @author ianw3214
 *
 */
public abstract class RootDialogue implements Dialogue {
	
	// A factory function to create copies
	public abstract Dialogue create();

	// default factory function
	public static RootDialogue createDefault() {
		return new EchoRootDialogue();
	}
	
}
