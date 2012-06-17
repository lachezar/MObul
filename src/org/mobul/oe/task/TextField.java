package org.mobul.oe.task;

import org.json.JSONArray;
import org.json.JSONException;
import org.mobul.R;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class TextField extends Field {

	public final static String[] COMPOSITION = new String[] { "text" };

	protected EditText editText;
	
	protected String text;

	public TextField(Activity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
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

		editText = (EditText) vg.getChildAt(1);

		return v;
	}

	@Override
	public View appendTo(LayoutInflater inflater, ViewGroup root) {
		return createUI(inflater, root, R.layout.field_text);
	}

	@Override
	public String toString() {
		JSONArray json = new JSONArray();
		json.put(type);
		json.put(id);
		json.put(editText.getText().toString());
		return json.toString();
	}

	@Override
	public void fromString(String str) {
		if (str.length() == 0) {
			return;
		}
		try {
			JSONArray json = new JSONArray(str);
			if (json.length() <= Field.HEADER_OFFSET) {
				return;
			}
			text = json.getString(Field.HEADER_OFFSET);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void reset() {
		editText.setText("");

	}

	@Override
	public boolean isValid() {
		return !required || !editText.getText().toString().equals("");
	}

	@Override
	public void populateField() {
		editText.setText(text);		
	}
	
}
