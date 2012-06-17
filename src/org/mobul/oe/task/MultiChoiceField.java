package org.mobul.oe.task;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.mobul.R;
import org.mobul.utils.StringUtils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

public class MultiChoiceField extends ChoiceField {

	public final static String[] COMPOSITION = new String[] { "multichoice" };

	protected HashMap<Map.Entry<Integer, String>, CheckBox> cbMap;
	
	protected Set<Integer> selectedSet;

	public MultiChoiceField(Activity activity) {
		super(activity);
		
		selectedSet = new HashSet<Integer>();
	}

	@Override
	public View appendTo(LayoutInflater inflater, ViewGroup root) {
		View v = inflater.inflate(R.layout.field_multichoice, root);

		ViewGroup vg = (ViewGroup) ((ViewGroup) v).getChildAt(index);

		TextView descriptionView = (TextView) vg.getChildAt(0);
		String description = this.description;
		if (required) {
			description += "*";
		}
		description += ": ";
		descriptionView.setText(description);

		ViewGroup mcvg = (ViewGroup) vg.getChildAt(1);

		cbMap = new HashMap<Map.Entry<Integer, String>, CheckBox>();

		for (Map.Entry<Integer, String> c : choices.entrySet()) {
			CheckBox cb = new CheckBox(inflater.getContext());
			cb.setText(c.getValue());
			mcvg.addView(cb);
			cbMap.put(c, cb);
		}

		return v;
	}

	@Override
	public String toString() {
		LinkedList<String> checked = new LinkedList<String>();

		for (Map.Entry<Map.Entry<Integer, String>, CheckBox> p : cbMap.entrySet()) {
			if (p.getValue().isChecked()) {
				checked.add(String.valueOf(p.getKey().getKey()));
			}
		}

		JSONArray json = new JSONArray();
		json.put(type);
		json.put(id);
		json.put(StringUtils.join(checked, ","));

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

			String list = json.getString(Field.HEADER_OFFSET);
			if (list == null || list.equals("")) {
				return;
			}

			String[] selectedList = list.split(",");
			for (String s : selectedList) {
				try {
					selectedSet.add(Integer.parseInt(s));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
					
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void reset() {
		for (Map.Entry<Map.Entry<Integer, String>, CheckBox> p : cbMap.entrySet()) {
			p.getValue().setChecked(false);
		}
	}

	@Override
	public boolean isValid() {
		if (!required)
			return true;
		for (Map.Entry<Map.Entry<Integer, String>, CheckBox> p : cbMap.entrySet()) {
			if (p.getValue().isChecked()) {
				return true;
			}
		}

		return false;
	}
	
	@Override
	public void populateField() {
		Integer[] ids = new Integer[selectedSet.size()];
		selectedSet.toArray(ids);
		for (Map.Entry<Map.Entry<Integer, String>, CheckBox> p : cbMap.entrySet()) {
			if (selectedSet.contains(p.getKey().getKey())) {
				p.getValue().setChecked(true);
			}
		}
	}

}
