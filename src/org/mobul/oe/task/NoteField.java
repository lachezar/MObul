package org.mobul.oe.task;

import org.mobul.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NoteField extends TextField {

	public final static String[] COMPOSITION = new String[] { "text" };

	public NoteField(Activity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View appendTo(LayoutInflater inflater, ViewGroup root) {
		return createUI(inflater, root, R.layout.field_note);
	}

}
