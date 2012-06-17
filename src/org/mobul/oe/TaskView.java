package org.mobul.oe;

import java.io.File;
import org.json.JSONException;
import org.json.JSONObject;
import org.mobul.R;
import org.mobul.db.DataHelper;
import org.mobul.db.ObservationData.Model;
import org.mobul.helpers.CommonAsyncTask;
import org.mobul.helpers.ProgressBarAsyncTask;
import org.mobul.oe.task.Field;
import org.mobul.oe.task.TaskForm;
import org.mobul.oe.task.PhotoField;
import org.mobul.utils.ImageUtils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class TaskView extends Activity {

	private static final String TAG = "TaskView";

	protected ScrollView scrollView;
	protected int scrollX;
	protected int scrollY;
	protected TextView titleView;
	protected TextView descriptionView;
	protected TextView observationsCountView;
	protected ViewGroup fieldsContainer;
	protected Field[] fields;
	protected Button saveButton;
	protected TaskForm task;

	private DataHelper dh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.task_view);

		scrollView = (ScrollView) findViewById(R.id.task_scroll);
		titleView = (TextView) findViewById(R.id.task_title);
		descriptionView = (TextView) findViewById(R.id.task_description);
		observationsCountView = (TextView) findViewById(R.id.task_observations_count);
		fieldsContainer = (ViewGroup) findViewById(R.id.task_fields);
		saveButton = (Button) findViewById(R.id.task_save_button);

		dh = new DataHelper(this);

		Bundle bundle = this.getIntent().getExtras();
		final String taskRepr = bundle.getString("task");

		new CommonAsyncTask(this).execute(new Runnable() {
			public void run() {
				populate(taskRepr);				
			}
		});		
		
		Log.i(TAG, "onCreate");
	}
	
	private void setObservationsCount() {
		int count = dh.getObservationData().observationsCountByTaskId(task.getId());
		if (count > 0) {
			observationsCountView.setText(count + " " + getString(R.string.pending_observations));
		} else {
			observationsCountView.setText("");
		}
	}
	
	public Field[] getFields() {
		return fields;
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		setObservationsCount();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// dh.close();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i(TAG, "onResume " + scrollY);
		scrollView.scrollTo(scrollX, scrollY);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		scrollX = scrollView.getScrollX();
		scrollY = scrollView.getScrollY();
		Log.i(TAG, "onPause " + scrollY);

		task.saveNotReadyObservation();
		
		finishFields();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);

	}

	public void populate(String taskRepr) {
		try {
			JSONObject json = new JSONObject(taskRepr);
			task = TaskForm.generateTask(json, this);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		fields = task.getFields();
		
		task.retrieveLastObservation();

		final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		runOnUiThread(new Runnable() {
			
			public void run() {
				titleView.setText(task.getTitle());
				descriptionView.setText(task.getDescription());
				setObservationsCount();
				
				for (int i = 0; i < task.getFields().length; i++) {
					Field f = task.getFields()[i];
					f.appendTo(inflater, fieldsContainer);
					f.populateField();
				}
				
			}
		});

		saveButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (!task.isValid()) {
					Toast.makeText(TaskView.this,
							R.string.complete_all_fields, 4000)
							.show();
				} else {
					boolean result = task.saveObservation();
					Log.i(TAG, "Save observation data " + result);
					task.retrieveLastObservation();
					task.reset();
					
					ConnectivityManager connectivityManager = (ConnectivityManager) TaskView.this.getSystemService(Context.CONNECTIVITY_SERVICE);
					boolean isWifi = (connectivityManager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI); 
					
					if (isWifi) {
						Model m = dh.getObservationData().selectLastInserted(task.getId());
						new ProgressBarAsyncTask(TaskView.this, dh.getObservationData()).execute(new Model[]{m});
					} else {
						TaskView.this.finish();
					}

				}
			}
		});
		
	}
	
	public void finishFields() {
		for (Field f : fields) {
			f.finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode >= Field.ACTIVITY_RESULT_BASE && requestCode < Field.ACTIVITY_RESULT_BASE + 1000) {
			PhotoField photoField = (PhotoField) fields[requestCode	- Field.ACTIVITY_RESULT_BASE];
			if (resultCode == Activity.RESULT_OK) {
				File imageFile = photoField.getImageFile();
				
				Bitmap pic = ImageUtils.get1mpBitmapFromFile(imageFile);
				
				ImageUtils.saveJPG(pic, imageFile, 90);

				photoField.setResult(imageFile);
			} else if (resultCode == Activity.RESULT_CANCELED) {
				photoField.setResult(null);
				// do smthing
			}
		}
	}

}
