package org.mobul.helpers;

import java.io.File;
import java.util.List;

import org.json.JSONException;
import org.mobul.R;
import org.mobul.db.DataTypes;
import org.mobul.db.ObservationData;
import org.mobul.db.ObservationData.Model;
import org.mobul.network.Communicator;
import org.mobul.network.FormBuilder;
import org.mobul.network.SubmitObservationCommunicator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class ProgressBarAsyncTask extends AsyncTask<Model, Integer, Integer> {
	
	private static final String TAG = "ProgressBarAsyncTask";
	
	private Activity activity;
	private ObservationData od;
	private ProgressDialog progressDialog;
	private SubmitObservationCommunicator comm;
	private boolean isCancelled;

	public ProgressBarAsyncTask(Activity activity, ObservationData od) {
		this.activity = activity;
		this.od = od;
		isCancelled = false;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		activity.runOnUiThread(new Runnable() {
			
			public void run() {
				progressDialog = new ProgressDialog(activity);
				progressDialog.setTitle(R.string.sending_label);
				progressDialog.setCancelable(true);
				progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				progressDialog.setOnCancelListener(new OnCancelListener() {
					
					public void onCancel(DialogInterface dialog) {
						Log.i(TAG, "progressDialog.onCancel()");
						isCancelled = true;
						ProgressBarAsyncTask.this.cancel(true);						
					}
				});
				progressDialog.show();				
			}
		});
		
		comm = new SubmitObservationCommunicator();		
	}

	@Override
	protected Integer doInBackground(Model... ods) {
		
		int successful = 0;
		
		progressDialog.setMax(ods.length);
		
		for (int i = 0; i < ods.length; i++) {
			
			if (isCancelled() || isCancelled) {
				return 0;
			}
			
			Model m = ods[i];
			int resultCode = Communicator.FORMAT;
			List<String[]> formFields = null;
			try {
				formFields = FormBuilder.buildFormFields(m.taskId, m.serializedData);
				resultCode = comm.submitData(m.taskId, formFields);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				resultCode = Communicator.FORMAT;
			} catch (Exception e) {
				e.printStackTrace();
				resultCode = Communicator.NO_CONNECTION;
			}
			
			if (isCancelled() || isCancelled) {
				Log.i(TAG, "aborted submittion");
				return 0;
			}
			
			if (resultCode == Communicator.ERROR) {
				// Something bad has happened on the server
			}
			if (resultCode == Communicator.OK) {
				successful++;
			}
			if (resultCode == Communicator.OK || resultCode == Communicator.FORMAT) {
				od.deleteById(m.id);
				try {
					for (String[] field : formFields) {
						if (field[1].equals(DataTypes.PHOTO)) {
							File file = new File(field[2]);
							if (file.exists()) {
								file.delete();
							}
						}
					}	
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (resultCode == Communicator.NO_CONNECTION) {
				activity.runOnUiThread(new Runnable() {
					public void run() {
						progressDialog.dismiss();
						Toast.makeText(activity, R.string.no_connection, 5000).show();
					}
				});
				return successful;
			}
			
			progressDialog.setProgress(i+1);
			
			if (isCancelled() || isCancelled) {
				return 0;
			}

		}
		
		String msg = activity.getString(R.string.data_submitted);
		if (ods.length > successful) {
			msg = activity.getString(R.string.data_submitted_with_errors)
							.replace("%SUCCESSFUL%", String.valueOf(successful))
							.replace("%ERRORS%", String.valueOf(ods.length - successful));
				
		}
		
		final String toastMsg = msg;
		
		activity.runOnUiThread(new Runnable() {
			public void run() {
				progressDialog.dismiss();
				Toast.makeText(activity, toastMsg, 5000).show();
				
			}
		});
		
		return successful;
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		
		if (activity instanceof IFeedbackFromAsyncTask) {
			((IFeedbackFromAsyncTask)activity).onPostExecution();
		}
	}
	
	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
		isCancelled = true;
		comm.abort();
		Log.i(TAG, "onCancelled()");
	}
	
}
