package com.pershing.dialogue;

import com.pershing.event.WebHookEvent;

public class DialogueStack {

	Dialogue rootDialogue;
	
	public DialogueStack(Dialogue dialogue) {
		this.rootDialogue = dialogue;
	}
	
	public void handleEvent(WebHookEvent event, String userId) {
		rootDialogue.top().handleEvent(event, userId);
	}
	
}
