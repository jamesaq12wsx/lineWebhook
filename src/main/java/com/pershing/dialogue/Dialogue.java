package com.pershing.dialogue;

import java.util.List;

import com.pershing.message.Message;

/**
 * The base interface for creating a Dialogue to push onto the Dialogue Stack
 * 
 * @author ianw3214
 *
 */
public interface Dialogue {

	/**
	 * The general function that accepts/parses user input, then returns a list the
	 * 	desired response as a list of messages.
	 * 
	 * @param input		The input message of the user
	 * @param stack		The dialogue stack
	 * @return
	 */
	public List<Message> handleUserInput(Message input, DialogueStack stack);
	public <T> T getMetaData();
	public <T> void receiveMetaData(T param);
	
}
