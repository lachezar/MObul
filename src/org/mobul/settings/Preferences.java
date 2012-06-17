package org.mobul.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Preferences {
	
	private static final String LOGIN = "login";
	private static final String FILTER = "filter";
	
	public static void setAuthentication(Activity activity, String mail, String password) {
		SharedPreferences login = activity.getSharedPreferences(LOGIN, Context.MODE_PRIVATE);
		Editor loginEditor = login.edit();
		loginEditor.putString("mail", mail);
		loginEditor.putString("password", password);
		loginEditor.commit();
	}
	
	public static String[] getAuthentication(Activity activity) {
		SharedPreferences login = activity.getSharedPreferences(LOGIN, Context.MODE_PRIVATE);
		return new String[]{login.getString("mail", null), login.getString("password", null)};
	}
	
	public static void setFilter(Activity activity, String country, String province, String city, boolean worldwide,
			String title, String tags, int order) {
		SharedPreferences filter = activity.getSharedPreferences(FILTER, Context.MODE_PRIVATE);
		Editor filterEditor = filter.edit();
		filterEditor.putString("country", country);
		filterEditor.putString("province", province);
		filterEditor.putString("city", city);
		filterEditor.putString("worldwide", String.valueOf(worldwide));
		filterEditor.putString("title", title);
		filterEditor.putString("tags", tags);
		filterEditor.putString("order", String.valueOf(order));
		filterEditor.commit();
	}
	
	public static String[] getFilter(Activity activity) {
		SharedPreferences filter = activity.getSharedPreferences(FILTER, Context.MODE_PRIVATE);
		return new String[]{filter.getString("country", ""), filter.getString("province", ""),
				filter.getString("city", ""), filter.getString("worldwide", ""),
				filter.getString("title", ""), filter.getString("tags", ""),
				filter.getString("order", "0")
				};
	}

}
