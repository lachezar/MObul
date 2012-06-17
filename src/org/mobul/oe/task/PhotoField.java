package org.mobul.oe.task;

import java.io.File;
import org.json.JSONArray;
import org.json.JSONException;
import org.mobul.R;
import org.mobul.utils.ImageUtils;
import org.mobul.utils.StringUtils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotoField extends Field {

	public final static String[] COMPOSITION = new String[] { "image" };

	@SuppressWarnings("unused")
	private static final String TAG = "PhotoField";

	protected ImageView imageView;
	protected File imageFile;
	
	protected Bitmap bmp;

	public PhotoField(Activity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View appendTo(LayoutInflater inflater, ViewGroup root) {
		View v = inflater.inflate(R.layout.field_photo, root);

		ViewGroup vg = (ViewGroup) ((ViewGroup) v).getChildAt(index);

		TextView descriptionView = (TextView) vg.getChildAt(0);
		String description = this.description;
		if (required) {
			description += "*";
		}
		description += ": ";
		descriptionView.setText(description);

		imageView = (ImageView) vg.getChildAt(1);

		ViewGroup buttons = (ViewGroup)vg.getChildAt(2);
		Button button = (Button) buttons.getChildAt(0);
		button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				if (imageFile == null) {
					imageFile = ImageUtils.generateImageFile();
				}
				camera.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
				activity.startActivityForResult(camera,	Field.ACTIVITY_RESULT_BASE + index);
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

	public void setResult(File f) {
		if (f == null) {
			return;
		}
		imageFile = f;

		populateField();
	}

	public File getImageFile() {
		return imageFile;
	}

	@Override
	public String toString() {
		return jsonArray().toString();
	}

	protected JSONArray jsonArray() {
		JSONArray json = new JSONArray();
		json.put(type);
		json.put(id);
		if (imageFile != null) {
			json.put(imageFile.getAbsolutePath());
		} else {
			json.put("");
		}

		return json;
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
			String path = json.getString(Field.HEADER_OFFSET);
			if (StringUtils.isEmpty(path)) {
				return;
			}
			imageFile = new File(path);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			imageFile = null;
		}
	}

	@Override
	public void reset() {
		reset(false);
	}
	
	public void reset(boolean deleteFile) {
		if (imageFile != null && deleteFile) {
			imageFile.delete();
		}	
		imageFile = null;
		imageView.setImageBitmap(null);
	}

	@Override
	public boolean isValid() {
		return !required || imageFile != null;
	}
	
	@Override
	public long getDataSize() {
		if (imageFile == null) {
			return 0;
		}
		
		return imageFile.length();
	}
	
	@Override
	public void populateField() {
		if (imageFile != null) {
			Display display = activity.getWindowManager().getDefaultDisplay();
			int max = display.getWidth()/2;
			Bitmap thumb = ImageUtils.getBitmapFromFile(imageFile, max);
			imageView.setImageBitmap(thumb);
			
		}
	}

}
