package org.mobul.network;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.mobul.utils.StringUtils;

public class FormBuilder {

	public static List<String[]> buildFormFields(long taskId, String jsonContent)
			throws JSONException {
		List<String[]> formFields = new LinkedList<String[]>();

		JSONArray json = new JSONArray(jsonContent);

		for (int i = 0; i < json.length(); i++) {
			JSONArray jsonField = json.getJSONArray(i);
			String key = jsonField.getString(1);
			String[] fragments = jsonField.getString(0).split("-");

			boolean isValidGroup = true;
			String[] vals = new String[fragments.length];
			for (int j = 0; j < fragments.length; j++) {
				vals[j] = jsonField.optString(j + 2);
				if (StringUtils.isEmpty(vals[j])) {
					isValidGroup = false;
				}
			}
			if (isValidGroup) {
				if (fragments.length == 1) {
					formFields.add(new String[] { key,	fragments[0], vals[0] });
				} else {
					for (int j = 0; j < fragments.length; j++) {
						formFields.add(new String[] { key + "-" + fragments[j],	fragments[j], vals[j] });
					}
				}
			}
		}

		return formFields;
	}

}
