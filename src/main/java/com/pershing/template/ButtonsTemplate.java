package com.pershing.template;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.pershing.action.Action;

/**
 * Class representing a button template object
 * 	- See more documentation @ https://developers.line.me/en/docs/messaging-api/reference/#buttons
 * 
 * @author ianw3214
 *
 */
public class ButtonsTemplate implements Template {

	private String thumbnailImageUrl;		// optional
	private String imageAspectRatio;		// optional
	private String imageSize;				// optional
	private String imageBackgroundColour;	// optional
	private String title;					// optional
	private String text;
	private Action defaultAction;			// optional
	private List<Action> actions;
	
	// default constructor
	public ButtonsTemplate() {
		thumbnailImageUrl = "";
		imageAspectRatio = "";
		imageSize = "";
		imageBackgroundColour = "";
		title = "";
		text = "";
		defaultAction = null;
		actions = null;
	}
	
	// constructor with all parameters for builder use
	public ButtonsTemplate(
			String newThumbnailImageUrl,
			String newImageAspectRatio,
			String newImageSize,
			String newImageBackgroundColour,
			String newTitle,
			String newText,
			Action newDefaultAction,
			List<Action> newActions) 
	{
		thumbnailImageUrl = newThumbnailImageUrl;
		imageAspectRatio = newImageAspectRatio;
		imageSize = newImageSize;
		imageBackgroundColour = newImageBackgroundColour;
		title = newTitle;
		text = newText;
		defaultAction = newDefaultAction;
		actions = newActions;
	}
	
	// builder class to help keep track of buttonstemplate instantiation
	static public class ButtonsTemplateBuilder {
		private String nestedThumbnailImageUrl;		// optional
		private String nestedImageAspectRatio;		// optional
		private String nestedImageSize;				// optional
		private String nestedImageBackgroundColour;	// optional
		private String nestedTitle;					// optional
		private String nestedText;
		private Action nestedDefaultAction;			// optional
		private List<Action> nestedActions;			// optional
		
		// builder constructor
		public ButtonsTemplateBuilder(String text) {
			nestedThumbnailImageUrl = "";
			nestedImageAspectRatio = "";
			nestedImageSize = "";
			nestedImageBackgroundColour = "";
			nestedTitle = "";
			nestedText = text;
			nestedDefaultAction = null;
			nestedActions = null;
		}
		
		// thumbnailImageUrl
		public ButtonsTemplateBuilder thumbNailImageUrl(String url) {
			nestedThumbnailImageUrl = url;
			return this;
		}
		
		// imageAspectRatio
		public ButtonsTemplateBuilder imageAspectRatio(String ratio) {
			nestedImageAspectRatio = ratio;
			return this;
		}
		
		// imageSize
		public ButtonsTemplateBuilder imageSize(String size) {
			nestedImageSize = size;
			return this;
		}
		
		// imageBackgroundColour
		public ButtonsTemplateBuilder imageBackgroundColour(String colour) {
			nestedImageBackgroundColour = colour;
			return this;
		}
		
		// title
		public ButtonsTemplateBuilder title(String title) {
			nestedTitle = title;
			return this;
		} 
		
		// text
		public ButtonsTemplateBuilder text(String text) {
			nestedText = text;
			return this;
		}
		
		// defaultAction
		public ButtonsTemplateBuilder defaultAction(Action action) {
			nestedDefaultAction = action;
			return this;
		}
		
		// actions
		public ButtonsTemplateBuilder actions(List<Action> actions) {
			nestedActions = actions;
			return this;
		}
		
		// add a single action
		public ButtonsTemplateBuilder addAction(Action action) {
			if (nestedActions == null) nestedActions = new ArrayList<Action>();
			// 4 is the maximum number of actions
			if (nestedActions.size() < 4) nestedActions.add(action);
			return this;
		}
		
		// create method
		public ButtonsTemplate build() {
			return new ButtonsTemplate(
					nestedThumbnailImageUrl,
					nestedImageAspectRatio,
					nestedImageSize,
					nestedImageBackgroundColour,
					nestedTitle,
					nestedText,
					nestedDefaultAction,
					nestedActions);
		}
		
	}
	
	/**
	 * Type getter method for calling program to handle messages correctly
	 */
	public TemplateType type() {
		return TemplateType.BUTTONS;
	}

	/**
	 * Setter method for the defaultAction that triggers when image is tapped
	 * @param action
	 */
	public void setDefaultAction(Action action) {
		defaultAction = action;
	}
	
	
	/**
	 * Setter method for the actions which represent each button of the template
	 * 	- Max action: 4
	 * @param newActions	List of the actions when buttons are tapped
	 */
	public void setActions(List<Action> newActions) {
		actions = newActions;
	}
	
	/**
	 * Add an action to the current list of actions
	 * 
	 * @param action		The new action to be added
	 */
	public void addAction(Action action) {
		if (actions != null) actions.add(action);
	}
	
	/**
	 * Returns the JSON representation of the buttons template
	 * @return	the JSON representation of the buttons template
	 */
	public JsonObject getAsJsonObject() {
		JsonObject obj = new JsonObject();
		// type
		obj.addProperty("type", "buttons");
		// thumbnailImageUrl
		if (!thumbnailImageUrl.equals("")) obj.addProperty("thumbnailImageUrl", thumbnailImageUrl);
		// imageAspectRatio
		if (!imageAspectRatio.equals("")) obj.addProperty("imageAspectRatio", imageAspectRatio);
		// imageSize
		if (!imageSize.equals("")) obj.addProperty("imageSize", imageSize);
		// imageBackgroundColour
		if (!imageBackgroundColour.equals("")) obj.addProperty("imageBackgroundColor", imageBackgroundColour);
		// title
		if (!title.equals("")) obj.addProperty("title", title);
		// text
		obj.addProperty("text", text);
		// defaultAction
		if (defaultAction != null) obj.add("defaultAction", defaultAction.getAsJsonObject());
		// actions
		JsonArray actionsArray = new JsonArray();
		if (actions != null) {
			for (Action a : actions) {
				actionsArray.add(a.getAsJsonObject());
			}
		}
		obj.add("actions", actionsArray);
		return obj;
	}

	/**
	 * Returns the JSON representation of the buttons template as a String
	 * @return	The string containing the JSON data of the buttons template
	 */
	public String getAsJsonString() {
		return getAsJsonObject().getAsString();
	}

}
