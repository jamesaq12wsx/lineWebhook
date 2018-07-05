package com.pershing.dialogue;

import java.util.ArrayList;
import java.util.List;

import com.pershing.message.Message;

/**
 * Sample instance of a dialogue class that echoes user inputs
 * 
 * @author ianw3214
 *
 */
public class EchoRootDialogue extends RootDialogue {

	public List<Message> handleUserInput(Message input, DialogueStack stack) {
		List<Message> result = new ArrayList<Message>();
		return null;
	}

	public <T> T getMetaData() {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> void receiveMetaData(T param) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Dialogue create() {
		return new EchoRootDialogue();
	}

}
