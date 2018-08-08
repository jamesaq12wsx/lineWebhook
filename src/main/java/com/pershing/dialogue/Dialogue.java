package com.pershing.dialogue;

import com.pershing.event.WebHookEvent;
import com.pershing.sender.MessageSender;

/**
 * Base dialogue class of a dialogue stack
 * 
 * @author ianw3214
 *
 */
public abstract class Dialogue {

	// The MessageSender for the dialogues to send messages with
	protected static MessageSender sender;
	
	// A reference to the previous dialogue
	private Dialogue previous;
	// A reference to the root dialogue
	protected Dialogue root;
	/**
	 * The top of the stack, only the root dialogue needs to worry about this so that the
	 * 	correct handler function can be called when necessary.
	 */
	private Dialogue top;
	
	// main event handler function
	public abstract void handleEvent(WebHookEvent event, String userId);
	
	/**
	 * Function to override if there is functionality desired for when an element is popped
	 * 	and the current dialogue is the next in the stack.
	 */
	protected void recieve() {
		// do nothing by default
	}
	
	/**
	 * Push a dialogue onto the stack
	 * 	- Set the previous to the current dialogue because when calling from within the
	 * 		class, the previous must be the current dialogue.
	 * 	- Set the root of the new dialogue to match the current root
	 * 	- Set the top of the root dialogue to be the new dialogue
	 * 
	 * @param dialogue
	 */
	protected void push(Dialogue dialogue) {
		dialogue.setPrevious(this);
		dialogue.setRoot(this.root);
		this.root.setTop(dialogue);
	}
	
	/**
	 * Remove a dialogue from the top of the stack
	 * 	- Simply update root.top to remove any references to the top dialogue, thus it will
	 * 		be garbage collected and the previous element becomes the new top
	 * 
	 * @return	whether a dialogue was successfully removed or not
	 */
	protected boolean pop() {
		if (this.previous == null) return false;
		this.root.top = this.previous;
		this.previous.recieve();
		return true;
	}
	
	// Setter methods
	public static void setSender(MessageSender sender) {
		Dialogue.sender = sender;
	}
	protected void setPrevious(Dialogue dialogue) {
		this.previous = dialogue;
	}
	protected void setRoot(Dialogue dialogue) {
		this.root = dialogue;
	}
	protected void setTop(Dialogue dialogue) {
		this.top = dialogue;
	}
	
	// Getter methods
	public Dialogue top() {
		return this.top;
	}
	
}
