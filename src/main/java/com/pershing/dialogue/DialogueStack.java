package com.pershing.dialogue;

import com.pershing.event.WebHookEvent;

/**
 * Container class that holds a stack of dialogues
 * 	- Since the stack structure is mainly stored within the dialogues themselves, there
 * 		isn't much implementation in the overall stack class, and is mainly used for
 * 		easier storage of dialogue stacks.
 * 
 * @author ianw3214
 *
 */
public class DialogueStack {

	// The root dialogue of the stack
	Dialogue rootDialogue;
	
	// Constructor of a dialogue stack which sets the root dialogue
	public DialogueStack(Dialogue dialogue) {
		this.rootDialogue = dialogue;
	}
	
	/**
	 * The general event handler function of the dialogue stack
	 * 	- Calls the handleEvent function of the current top of the stack
	 * 
	 * @param event		The incoming webhook event
	 * @param userId	The userId source of the event
	 */
	public void handleEvent(WebHookEvent event, String userId) {
		rootDialogue.top().handleEvent(event, userId);
	}
	
}
