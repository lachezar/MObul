package org.mobul.oe.task;

import java.util.Date;

import org.mobul.R;
import org.mobul.utils.TimeUtils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TimeField extends DateTimeField {

	public final static String[] COMPOSITION = new String[] { "datetime" };

	protected TextView timeTextView;
	protected Date date;

	public TimeField(Activity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View appendTo(LayoutInflater inflater, ViewGroup root) {
		return createUI(inflater, root, R.layout.field_time);

	}

	@Override
	protected String format(Date date) {
		if (date == null)
			return "";
		return TimeUtils.getTime(date);
	}

}
