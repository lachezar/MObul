package org.mobul.oe.task;

import java.io.File;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.mobul.R;
import org.mobul.helpers.QuickLocator;
import org.mobul.utils.ImageUtils;
import org.mobul.utils.TimeUtils;

import android.app.Activity;
import android.content.Intent;
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

public class PhotoGPSDateTimeField extends PhotoGPSField {

	public final static String[] COMPOSITION = new String[] { "image",
			"location", "datetime" };

	@SuppressWarnings("unused")
	private static final String TAG = "PhotoGPSDateTimeField";

	protected Date date;
	protected TextView dateTimeTextView;

	public PhotoGPSDateTimeField(Activity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View appendTo(LayoutInflater inflater, ViewGroup root) {
		View v = inflater.inflate(R.layout.field_photo_gps_datetime, root);

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
		dateTimeTextView = (TextView) vg.getChildAt(3);

		ViewGroup buttons = (ViewGroup)vg.getChildAt(4);
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
					ql = new QuickLocator(activity, PhotoGPSDateTimeField.this);
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
		if (f != null) {
			date = new Date();
			dateTimeTextView.setText(TimeUtils.getDateTime(date));
		}
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
		if (date != null) {
			json.put(date.getTime());
		}

		return json;
	}

	@Override
	public void fromString(String str) {
		if (str.length() == 0)
			return;

		super.fromString(str);

		try {
			JSONArray json = new JSONArray(str);
			if (json.length() <= Field.HEADER_OFFSET + 2) {
				return;
			}
			date = new Date(Long.parseLong(json.getString(Field.HEADER_OFFSET + 2)));
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
			date = null;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			date = null;
		}

	}

	@Override
	public void reset(boolean mode) {
		super.reset(mode);
		date = null;
		dateTimeTextView.setText("");
	}

	@Override
	public boolean isValid() {
		return !required || (super.isValid() && date != null);
	}
	
	@Override
	public void populateField() {
		super.populateField();
		dateTimeTextView.setText(TimeUtils.getDateTime(date));
	}

}
