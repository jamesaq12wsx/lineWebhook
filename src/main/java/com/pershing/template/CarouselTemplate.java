package com.pershing.template;

import java.util.List;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * A carousel is a template with multiple columns that can be cycles like a carousel
 * 	- See documentation @ https://developers.line.me/en/reference/messaging-api/#carousel
 * 
 * @author ianw3214
 *
 */
public class CarouselTemplate implements Template {

	// Array of columns used to construct the template
	private List<Column> columns;
	// The aspect ratio of the images in the individual columns
	private String imageAspectRatio;
	// Size of the image in the individual columns
	private String imageSize;
	
	/**
	 * Type getter method for calling program to handle messages correctly
	 */
	@Override
	public TemplateType type() {
		return TemplateType.CAROUSEL;
	}
	
	/**
	 * Add a column to the current list of columns
	 * @param action		The new action to be added
	 */
	public void addColumn(Column column) {
		if (this.columns == null) this.columns = new ArrayList<Column>();
		this.columns.add(column);
	}
	
	/**
	 * Setter method for the image aspect ratios of the column images
	 * @param aspectRatio	The aspect ratio to set the images to
	 */
	public void setImageAspectRatio(String aspectRatio) {
		this.imageAspectRatio = aspectRatio;
	}
	
	/**
	 * Setter method for the image size of the column images
	 * @param imageSize		The image size to set the images to
	 */
	public void setImageSize(String imageSize) {
		this.imageSize = imageSize;
	}

	/**
	 * Returns the JSON representation of the carousel template
	 * @return	the JSON representation of the carousel template
	 */
	@Override
	public JsonObject getAsJsonObject() {
		JsonObject result = new JsonObject();
		result.addProperty("type", "carousel");
		// Add the columns if there are any
		if (columns != null) {
			JsonArray columnsArr = new JsonArray();
			for (Column c : columns) {
				columnsArr.add(c.getAsJsonObject());
			}
			result.add("columns", columnsArr);
		}
		if (imageAspectRatio != null) result.addProperty("imageAspectRatio", imageAspectRatio);
		if (imageSize != null) result.addProperty("imageSize", imageSize);
		return result;
	}

	/**
	 * Returns the JSON representation of the carousel template as a String
	 * @return	The string containing the JSON data of the carousel template
	 */
	@Override
	public String getAsJsonString() {
		return getAsJsonObject().toString();
	}

}
