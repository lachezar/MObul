package org.mobul.oe.task;

import org.json.JSONArray;
import org.json.JSONException;
import org.mobul.R;
import org.mobul.helpers.ILocator;
import org.mobul.helpers.QuickLocator;
import org.mobul.utils.LocationUtils;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GPSField extends Field implements ILocator {

	public final static String[] COMPOSITION = new String[] { "location" };

	public GPSField(Activity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
	}

	private static final String TAG = "GPSField";

	private Context context;
	private QuickLocator ql = null;
	private TextView gpsTextView;
	private Location location = null;

	@Override
	public View appendTo(LayoutInflater inflater, ViewGroup root) {
		context = inflater.getContext();
		View v = inflater.inflate(R.layout.field_gps, root);

		ViewGroup vg = (ViewGroup) ((ViewGroup) v).getChildAt(index);

		TextView descriptionView = (TextView) vg.getChildAt(0);
		String description = this.description;
		if (required) {
			description += "*";
		}
		description += ": ";
		descriptionView.setText(description);

		gpsTextView = (TextView) vg.getChildAt(1);

		Button button = (Button) vg.getChildAt(2);
		button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				gpsTextView.setText("...");
				if (ql == null) {
					ql = new QuickLocator(activity, GPSField.this);
				} else {
					ql.stop();
				}

				ql.start();

			}
		});

		return v;
	}

	public void updateLocation(Location location) {
		this.location = location;

		if (location == null) {
			Toast toast = Toast.makeText(context, R.string.could_not_get_gps,	2000);
			toast.show();
		}

		gpsTextView.setText(LocationUtils.format(location, activity.getString(R.string.gps_is_unavailable)));

		Log.i(TAG, "new location " + toString());
	}
	
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	protected static String locationRepr(Location location) {
		if (location == null) {
			return "";
		} else {
			return location.getLongitude() + "," + location.getLatitude();
		}
	}

	protected void loadLocation(String str) {
		location = new Location("");
		String[] fix = str.split(",");
		if (fix.length == 2) {
			try {
				location.setLongitude(Double.valueOf(fix[0]));
				location.setLatitude(Double.valueOf(fix[1]));
			} catch (NumberFormatException e) {
				location = null;
			}
		}
	}

	@Override
	public String toString() {
		JSONArray json = new JSONArray();
		json.put(type);
		json.put(id);
		json.put(locationRepr(location));
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
			String locationString = json.getString(Field.HEADER_OFFSET);
			if (locationString == null || locationString.equals("")) {
				return;
			}
			loadLocation(locationString);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void reset() {
		location = null;
		gpsTextView.setText("");
	}

	@Override
	public boolean isValid() {
		return !required || location != null;
	}
	
	@Override
	public void populateField() {
		gpsTextView.setText(LocationUtils.format(location, ""));
	}
	
	@Override
	public void finish() {
		if (ql != null) {
			ql.stop();
		}
	}

}
