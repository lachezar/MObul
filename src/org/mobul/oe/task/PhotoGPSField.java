package org.mobul.oe.task;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.mobul.R;
import org.mobul.helpers.ILocator;
import org.mobul.helpers.QuickLocator;
import org.mobul.utils.ImageUtils;
import org.mobul.utils.LocationUtils;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PhotoGPSField extends PhotoField implements ILocator {

	public final static String[] COMPOSITION = new String[] { "image", "location" };

	@SuppressWarnings("unused")
	private static final String TAG = "PhotoGPSField";

	protected QuickLocator ql = null;
	protected TextView gpsTextView;
	protected Button renewGPSButton;
	protected Location location = null;
	protected Location newLocation;
	protected boolean newPhoto = false;

	public PhotoGPSField(Activity activity) {
		super(activity);
	}

	@Override
	public View appendTo(LayoutInflater inflater, ViewGroup root) {
		View v = inflater.inflate(R.layout.field_photo_gps, root);

		ViewGroup vg = (ViewGroup) ((ViewGroup) v).getChildAt(index);

		TextView descriptionView = (TextView) vg.getChildAt(0);
		String description = this.description;
		if (required) {
			description += "*";
		}
		description += ": ";
		descriptionView.setText(description);

		imageView = (ImageView) vg.getChildAt(1);
		gpsTextView = (TextView) vg.getChildAt(2);

		ViewGroup buttons = (ViewGroup)vg.getChildAt(3);
		Button button = (Button) buttons.getChildAt(0);
		button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				if (imageFile == null)
					imageFile = ImageUtils.generateImageFile();
				camera.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(imageFile));
				activity.startActivityForResult(camera,
						Field.ACTIVITY_RESULT_BASE + index);

				gpsTextView.setText("...");
				if (ql == null) {
					ql = new QuickLocator(activity, PhotoGPSField.this);
				} else {
					ql.stop();
				}

				ql.start();
			}
		});
		
		ImageButton resetButton = (ImageButton) buttons.getChildAt(1);
		resetButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				reset(true);
			}
		});

		return v;
	}

	@Override
	public void setResult(File f) {
		super.setResult(f);
		if (f == null) {
			ql.stop();
			ql = null;
			if (imageFile == null) {
				location = null;
				gpsTextView.setText("No GPS fix.");
			} else {
				gpsTextView.setText(LocationUtils.format(location, activity.getString(R.string.gps_is_unavailable)));
			}
		} else {
			if (newLocation != null) {
				location = newLocation;
				newLocation = null;
				gpsTextView.setText(LocationUtils.format(location, activity.getString(R.string.gps_is_unavailable)));
			} else {
				gpsTextView.setText("Please wait, looking for a GPS fix.");
				Toast.makeText(activity, "Please wait, looking for a GPS fix.", 5000).show();
				newPhoto = true;
			}

			// location = tmpLocation;
		}

	}

	public void updateLocation(Location location) {

		if (location == null) {
			gpsTextView.setText(LocationUtils.format(location, activity.getString(R.string.gps_is_unavailable)));
			Toast toast = Toast.makeText(activity, R.string.could_not_get_gps, 5000);
			toast.show();
//			if (newPhoto) {
//				renewGPSButton.setVisibility(View.VISIBLE);
//			}
		} else {

			if (newPhoto) {
				this.location = location;
				gpsTextView.setText(LocationUtils.format(location, activity.getString(R.string.gps_is_unavailable)));
				newPhoto = false;
			} else {
				newLocation = location;
			}
			
			//renewGPSButton.setVisibility(View.INVISIBLE);
		}

	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return jsonArray().toString();
	}

	@Override
	protected JSONArray jsonArray() {
		JSONArray json = super.jsonArray();
		try {
			json.put(0, type);
			json.put(1, id);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			json.put(type);
			json.put(id);
		}
		json.put(GPSField.locationRepr(location));

		return json;
	}

	@Override
	public void fromString(String str) {
		if (str.length() == 0)
			return;

		super.fromString(str);

		try {
			JSONArray json = new JSONArray(str);
			if (json.length() <= Field.HEADER_OFFSET + 1) {
				return;
			}
			loadLocation(json.getString(Field.HEADER_OFFSET + 1));
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			location = null;
			imageFile = null;
			e.printStackTrace();
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
		} else {
			location = null;
		}
	}

	@Override
	public void reset(boolean mode) {
		// TODO Auto-generated method stub
		super.reset(mode);
		location = null;
		gpsTextView.setText("");
	}
	
	@Override
	public boolean isValid() {
		return !required || (super.isValid() && location != null);
	}
	
	@Override
	public void populateField() {
		super.populateField();
		gpsTextView.setText(LocationUtils.format(location, "No GPS fix.")); //"Getting GPS fix. Please, wait..."));
	}

}
