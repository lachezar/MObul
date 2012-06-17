package org.mobul.oe.task;

import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.mobul.R;
import org.mobul.utils.TimeUtils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DateTimeField extends Field {

	public final static String[] COMPOSITION = new String[] { "datetime" };

	protected TextView dateTimeTextView;
	protected Date date;

	public DateTimeField(Activity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View appendTo(LayoutInflater inflater, ViewGroup root) {
		return createUI(inflater, root, R.layout.field_datetime);
	}

	protected View createUI(LayoutInflater inflater, ViewGroup root, int layout) {
		View v = inflater.inflate(layout, root);

		ViewGroup vg = (ViewGroup) ((ViewGroup) v).getChildAt(index);

		TextView descriptionView = (TextView) vg.getChildAt(0);
		String description = this.description;
		if (required) {
			description += "*";
		}
		description += ": ";
		descriptionView.setText(description);

		dateTimeTextView = (TextView) vg.getChildAt(1);

		Button button = (Button) vg.getChildAt(2);
		button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				date = new Date();
				dateTimeTextView.setText(format(date));
			}
		});

		return v;
	}

	protected String format(Date date) {
		if (date == null)
			return "";
		return TimeUtils.getDateTime(date);
	}

	@Override
	public String toString() {
		JSONArray json = new JSONArray();
		json.put(type);
		json.put(id);

		String datetext = "";
		if (date != null) {
			datetext = String.valueOf(date.getTime());
		}
		json.put(datetext);

		return json.toString();
	}

	@Override
	public void fromString(String str) {
		if (str.length() == 0)
			return;
		try {
			JSONArray json = new JSONArray(str);
			if (json.length() <= Field.HEADER_OFFSET)
				return;
			String v = json.getString(Field.HEADER_OFFSET);
			if (v == null || v.equals(""))
				return;
			date = new Date(Long.parseLong(v));
			
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void reset() {
		date = null;
		dateTimeTextView.setText("");
	}

	@Override
	public boolean isValid() {
		return !required || date != null;
	}
	
	@Override
	public void populateField() {
		dateTimeTextView.setText(format(date));		
	}

}
