package org.mobul.network;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mobul.db.AbstractObservationEvent.Model;

public class REST {
	
	public static int getResultFromResponse(String response) {

		int res = -1;

		try {
			JSONObject json = new JSONObject(response);
			res = json.getInt("validationStatus");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}
	
	public static List<Model> getOEs(String response) {
		List<Model> l = new LinkedList<Model>();

		JSONArray jsonRoot = null;
		try {
			jsonRoot = new JSONArray(response);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return l;
		}
		
		for (int i = 0; i < jsonRoot.length(); i++) {
			try {
				JSONObject oe = jsonRoot.getJSONObject(i);
			
				Model m = new Model(null, String.valueOf(oe.getLong("id")), oe.getString("title"),
						oe.getString("description"), oe.getString("tasks"), oe.getString("tags"),
						oe.getString("country"), oe.getString("province"), oe.getString("city"), 
						0L, oe.getLong("version"));
				l.add(m);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return l;
	}

}
