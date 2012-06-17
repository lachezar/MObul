package org.mobul.oe;

import org.mobul.R;
import org.mobul.db.AbstractObservationEvent.Model;
import org.mobul.helpers.CommonAsyncTask;
import org.mobul.network.Communicator;
import org.mobul.network.OESubscription;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class PublicObservationEventView extends ObservationEventView {
	
	private static final String TAG = "PublicObservationEventView"; 
	
	public PublicObservationEventView() {
		super();
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		final Model publicModel = dh.getPublicObservationEvent().selectById(id);
		Model myModel = dh.getMyObservationEvent().selectByKey(publicModel.key);
		
		if (publicModel != null && myModel == null) {
		
			oeActionButton.setText(R.string.start_participating);
			
			oeActionButton.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					
					new CommonAsyncTask(PublicObservationEventView.this).execute(new Runnable() {
						
						public void run() {
							int resultCode = new OESubscription().subscribe(publicModel.key);
							Log.i(TAG, "Result Code: " + resultCode);
							if (resultCode == Communicator.OK) {							
								synchronized(dh.getMyObservationEvent()) {
									
									if (dh.getMyObservationEvent().selectByKey(publicModel.key) == null) {
										dh.getMyObservationEvent().insert(publicModel.key, publicModel.title, publicModel.description, 
												publicModel.observationDataRequest, publicModel.tags, publicModel.country, publicModel.province, 
												publicModel.city, publicModel.technicalParams, publicModel.version);
									}
								}
								finish();
							} else {
								runOnUiThread(new Runnable() {								
									public void run() {
										Toast.makeText(PublicObservationEventView.this, R.string.can_not_subscribe, 3000).show();
									}
								});							
							}
						}
					});
					
				}
			});
		}
		
		if (myModel != null) {
			oeActionButton.setVisibility(View.INVISIBLE);
		} else {
			oeActionButton.setVisibility(View.VISIBLE);
		}
	}

}
