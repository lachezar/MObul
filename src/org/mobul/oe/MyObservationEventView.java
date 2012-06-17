package org.mobul.oe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mobul.R;
import org.mobul.db.AbstractObservationEvent.Model;
import org.mobul.helpers.CommonAsyncTask;
import org.mobul.network.Communicator;
import org.mobul.network.OESubscription;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MyObservationEventView extends ObservationEventView {
	
	public MyObservationEventView() {
		// TODO Auto-generated constructor stub
		super();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		oeActionButton.setText(R.string.stop_participating);
		
		oeActionButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				new AlertDialog.Builder(MyObservationEventView.this).setTitle(R.string.stop_participating)
				.setMessage(R.string.stop_participating_confirmation)
				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					
		            public void onClick(DialogInterface dialog, int which) {
		            	new CommonAsyncTask(MyObservationEventView.this).execute(new Runnable() {
							
							public void run() {
								Model m = dh.getMyObservationEvent().selectById(id);
								if (m == null) {
									return;
								}
								
								int resultCode = new OESubscription().unsubscribe(m.key);
								if (resultCode == Communicator.OK) {
									removeOE(id);
								} else {
									runOnUiThread(new Runnable() {								
										public void run() {
											Toast.makeText(MyObservationEventView.this, R.string.can_not_unsubscribe, 3000).show();
										}
									});
								}
							}
						});
		            	
		            }
	
		        }).setNegativeButton(R.string.no, null).show();
				
			}
		});
	}
	
	private void removeOE(long id) {
		Model m = dh.getMyObservationEvent().selectById(id);

		try {
			JSONArray json = new JSONArray(m.observationDataRequest);
			for (int i = 0; i < json.length(); i++) {
				try {
					JSONObject task = json.getJSONObject(i);
					long taskId = task.getLong("id");
					dh.getObservationData().deleteReadyByTaskId(taskId);
					Log.i(TAG, "delete observation for task " + taskId);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		dh.getMyObservationEvent().deleteById(id);				
		finish();
	}

}
