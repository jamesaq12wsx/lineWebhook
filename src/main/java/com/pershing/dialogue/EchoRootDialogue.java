package com.pershing.dialogue;

import java.util.ArrayList;
import java.util.List;

import com.pershing.message.Message;
import com.pershing.message.MessageType;
import com.pershing.message.TextMessage;

/**
 * Sample instance of a dialogue class that echoes user inputs
 * 
 * @author ianw3214
 *
 */
public class EchoRootDialogue extends RootDialogue {

	public List<Message> handleUserInput(Message input, DialogueStack stack) {
		List<Message> result = new ArrayList<Message>();
		// send an echo message if the input was a text message
		if (input.type() == MessageType.TEXT) {
			TextMessage textInput = (TextMessage) input;
			result.add(new TextMessage(textInput.getText()));
		} else {
			TextMessage text = new TextMessage("MESSAGE RECIEVED: " + input.type().toString());
			result.add(text);
		}
		return result;
	}

	// This dialogue frame returns no metadata
	public <T> T getMetaData() {
		return null;
	}

	// This dialogue frame does nothing with recieved metadata
	public <T> void receiveMetaData(T param) {
		
	}

	/**
	 * Factory method for creating default dialogues
	 */
	@Override
	public Dialogue create() {
		return new EchoRootDialogue();
	}

}
