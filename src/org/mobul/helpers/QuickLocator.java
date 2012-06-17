package org.mobul.helpers;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class QuickLocator implements LocationListener {

	private static final String TAG = "QuickLocator";

	public QuickLocator(Activity activity, ILocator locator) {
		this.locator = locator;
		this.activity = activity;

		lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
		timer = new Timer();
	}

	private Activity activity;
	private LocationManager lm;
	private ILocator locator;
	private Location location = null;
	private Date date;
	private Timer timer;
	private TimerTask onLocationChangeTimer;
	private static final long TIMEOUT = 30 * 1000;
	private static final float ACCURACY = 50;

	public void onLocationChanged(Location location) {
		Log.i(TAG, "new location");

		if (location != null) {
			this.location = location;
			Log.i(TAG, location.getLongitude() + " " + location.getLatitude());
		}

		Log.i(TAG, location + "" + new Date().getTime() + " "
				+ (date.getTime() + TIMEOUT));

		if ((location != null && location.hasAccuracy() && location.getAccuracy() <= ACCURACY)
				|| (new Date().getTime() > date.getTime() + TIMEOUT)) {
			activity.runOnUiThread(new Runnable() {

				public void run() {
					locator.updateLocation(QuickLocator.this.location);
				}
			});

			stop();
		}

	}

	public void stop() {
		this.date.setTime(0L);
		onLocationChangeTimer.cancel();
		lm.removeUpdates(this);
	}

	public void start() {
		this.date = new Date();
		Criteria gps = new Criteria();
		gps.setAccuracy(Criteria.ACCURACY_FINE);
		lm.requestLocationUpdates(lm.getBestProvider(gps, true), 1000, 2, this);

		onLocationChangeTimer = callOnLocationChanged();
		timer.schedule(onLocationChangeTimer, TIMEOUT + 1000);
	}

	private TimerTask callOnLocationChanged() {
		return new TimerTask() {

			@Override
			public void run() {
				onLocationChanged(null);
			}

		};
	}

	public Location getLocation() {
		return location;
	}

	public void onProviderDisabled(String provider) {
		locator.updateLocation(null);

	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
}
