package org.mobul.utils;

import android.location.Location;

public class LocationUtils {

	public static String format(Location l, String unavailableMessage) {
		if (l == null) {
			return unavailableMessage;
		}
		return formatAsDegree(l.getLongitude()) + " : "
				+ formatAsDegree(l.getLatitude());
	}

	public static String formatAsDegree(double d) {
		int main = (int) d;
		double remainder = d - main;
		if (remainder < 0) {
			remainder *= -1d;
		}
		
		int minutes = (int) (remainder * 60d);
		double remainder2 = (remainder * 60d) - minutes;
		
		int seconds = (int) (remainder2 * 60d);
		
		return main + "°" + minutes + "'" + seconds + "\"";

	}

}
