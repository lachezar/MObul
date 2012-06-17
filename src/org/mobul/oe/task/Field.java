package org.mobul.oe.task;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class Field {

	protected String id;
	protected int index;
	protected String type;
	protected String description;
	protected boolean required;
	protected Activity activity;

	public final static String[] COMPOSITION = new String[] {};
	public final static int HEADER_OFFSET = 2;

	public static final int ACTIVITY_RESULT_BASE = 10000;

	public Field(Activity activity) {
		this.activity = activity;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public abstract View appendTo(LayoutInflater inflater, ViewGroup root);

	public abstract boolean isValid();

	public abstract void fromString(String str);

	public abstract void reset();
	
	public long getDataSize() {
		return 0;
	}
	
	public abstract void populateField();
	
	public void finish() {};

}
