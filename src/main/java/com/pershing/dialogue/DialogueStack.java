package com.pershing.dialogue;

import java.util.ArrayList;
import java.util.List;

/**
 * A Stack data structure to for designing dialogue flow
 * @author ianw3214
 *
 */
public class DialogueStack {

	// Use a list to represent a stack
	private List<Dialogue> stack;
	// Keep track of the top of the stack via a counter
	private int top;
	
	/**
	 * Main constructor that initializes the dialogue stack with a root dialogue
	 * 
	 * @param root
	 */
	public DialogueStack(Dialogue root) {
		stack = new ArrayList<Dialogue>();
		stack.add(root);
		top = 0;
	}
	
	/**
	 * Factory to create a default stack dialogue with an echo bot root handler 
	 * 
	 * @return
	 */
	public static final DialogueStack createDefault() {
		return new DialogueStack(new EchoRootDialogue());
	}
	
	/**
	 * Pop operation that also handles the transfer of metadata between dialogues
	 * 
	 * @return	The previous top of the stack
	 */
	public Dialogue pop() {
		// Make sure we are not removing the root dialogue
		if (stack.size() <= 1) return null;
		// Execute the actual pop operation
		Dialogue dialogue = stack.remove(top--);
		// Handle the transfer of metadata
		stack.get(top).receiveMetaData(dialogue.getMetaData());
		return dialogue;
	}
	
	/**
	 * Default stack push operation
	 * 
	 * @param dialogue	The dialogue to be added to the stack
	 */
	public void push(Dialogue dialogue) {
		stack.add(dialogue);
		top++;
	}
	
	/**
	 * Getter method for inspecting the top of the stack
	 * 
	 * @return	The top element of the stack
	 */
	public Dialogue top() {
		return stack.get(top);
	}
	
}
