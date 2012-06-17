package org.mobul.oe.task;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.mobul.R;
import org.mobul.utils.StringUtils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class ChoiceField extends Field {

	public final static String[] COMPOSITION = new String[] { "choice" };

	private HashMap<Map.Entry<Integer, String>, RadioButton> rbMap;

	protected RadioGroup rg;
	
	protected int selectedId;

	public ChoiceField(Activity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
	}

	HashMap<Integer, String> choices;

	public HashMap<Integer, String> getChoices() {
		return choices;
	}

	public void setChoices(HashMap<Integer, String> choices) {
		this.choices = choices;
	}

	@Override
	public View appendTo(LayoutInflater inflater, ViewGroup root) {
		View v = inflater.inflate(R.layout.field_choice, root);

		ViewGroup vg = (ViewGroup) ((ViewGroup) v).getChildAt(index);

		TextView descriptionView = (TextView) vg.getChildAt(0);
		String description = this.description;
		if (required) {
			description += "*";
		}
		description += ": ";
		descriptionView.setText(description);

		rg = (RadioGroup) vg.getChildAt(1);

		rbMap = new HashMap<Map.Entry<Integer, String>, RadioButton>();

		for (Map.Entry<Integer, String> c : choices.entrySet()) {
			RadioButton rb = new RadioButton(inflater.getContext());
			rb.setText(c.getValue());
			rg.addView(rb);
			rbMap.put(c, rb);
		}

		return v;
	}

	@Override
	public String toString() {

		JSONArray json = new JSONArray();
		json.put(type);
		json.put(id);

		for (Map.Entry<Map.Entry<Integer, String>, RadioButton> p : rbMap.entrySet()) {
			if (p.getValue().isChecked()) {
				json.put(String.valueOf(p.getKey().getKey()));
				return json.toString();
			}
		}
		
		json.put("");

		return json.toString();
	}

	@Override
	public void fromString(String str) {
		if (str.length() == 0)
			return;

		try {
			JSONArray json = new JSONArray(str);
			if (json.length() <= Field.HEADER_OFFSET) {
				return;
			}
			String value = json.getString(Field.HEADER_OFFSET);
			if (!StringUtils.isEmpty(value)) {
				selectedId = Integer.parseInt(value);
			}
						
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void reset() {
		rg.clearCheck();

	}

	@Override
	public boolean isValid() {
		return !required || rg.getCheckedRadioButtonId() != -1;
	}
	
	@Override
	public void populateField() {
		for (Map.Entry<Integer, String> p : choices.entrySet()) {

			if (p.getKey() == selectedId) {
				rbMap.get(p).setChecked(true);
			}
		}	
	}

}
