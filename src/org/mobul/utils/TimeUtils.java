package org.mobul.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.text.format.DateFormat;

public class TimeUtils {

	public static String getDate(Date d) {
		if (d == null) {
			return "";
		}
		return (String) DateFormat.format("yyyy-MM-dd", d);
	}

	public static String getDate() {
		return getDate(new Date());
	}

	public static String getTime(Date d) {
		if (d == null) {
			return "";
		}
		return (String) DateFormat.format("kk:mm", d);
	}

	public static String getTime() {
		return getTime(new Date());
	}

	public static String getDateTime(Date d) {
		if (d == null) {
			return "";
		}
		return getDate(d) + " " + getTime(d);
	}

	public static String getDateTime() {
		return getDateTime(new Date());
	}

	public static String formatDBTimestamp(long ts) {
		return new SimpleDateFormat("yyyy-MM-dd").format(ts * 1000);
	}

}
