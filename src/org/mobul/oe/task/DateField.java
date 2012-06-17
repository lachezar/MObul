package org.mobul.oe.task;

import java.util.Date;

import org.mobul.R;
import org.mobul.utils.TimeUtils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DateField extends DateTimeField {

	public final static String[] COMPOSITION = new String[] { "datetime" };

	public DateField(Activity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View appendTo(LayoutInflater inflater, ViewGroup root) {
		return createUI(inflater, root, R.layout.field_date);

	}

	@Override
	protected String format(Date date) {
		if (date == null)
			return "";
		return TimeUtils.getDate(date);
	}

}
