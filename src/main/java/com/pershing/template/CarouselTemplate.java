package com.pershing.template;

import java.util.List;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class CarouselTemplate implements Template {

	private List<Column> columns;
	private String imageAspectRatio;
	private String imageSize;
	
	@Override
	public TemplateType type() {
		return TemplateType.CAROUSEL;
	}
	
	public void addColumn(Column column) {
		if (this.columns == null) this.columns = new ArrayList<Column>();
		this.columns.add(column);
	}
	
	public void setImageAspectRatio(String aspectRatio) {
		this.imageAspectRatio = aspectRatio;
	}
	
	public void setImageSize(String imageSize) {
		this.imageSize = imageSize;
	}

	@Override
	public JsonObject getAsJsonObject() {
		JsonObject result = new JsonObject();
		result.addProperty("type", "carousel");
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

	@Override
	public String getAsJsonString() {
		return getAsJsonObject().toString();
	}

}
