package org.mobul.helpers;

import org.mobul.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

public class CommonAsyncTask extends AsyncTask<Runnable, Void, Void> {

	protected Activity activity;
	protected ProgressDialog progressDialog;

	public CommonAsyncTask(Activity activity) {
		super();
		this.activity = activity;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		activity.runOnUiThread(new Runnable() {
			
			public void run() {
				progressDialog = new ProgressDialog(activity);
				progressDialog.setTitle(R.string.loading_label);
				progressDialog.setCancelable(false);
				progressDialog.show();				
			}
		});
	}

	@Override
	protected Void doInBackground(Runnable... params) {
		for (Runnable r : params) {
			r.run();
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		
		activity.runOnUiThread(new Runnable() {
			public void run() {
				progressDialog.dismiss();
			}
		});
	}

}
