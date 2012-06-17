package org.mobul.network;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.mobul.db.DataTypes;
import android.util.Log;

public class SubmitObservationCommunicator extends Communicator {
	
	public int submitData(long taskId, List<String[]> fields) {
		
		if (!isLoggedIn() && login() != OK) {
			return NO_CONNECTION;
		}
		
		String url = OBSERVATION_SUBMIT_URL.replace("%TASK_ID%", String.valueOf(taskId));

		Log.i(TAG, "Submit to: " + url);
		Log.i(TAG, "Fields count: " + fields.size());
		for (String[] f : fields) {
			Log.i(TAG, f[0] + " : " + f[1] + " : " + f[2]);
		}

		HttpPost httppost = getPostRequest(url);		
		
		MultipartEntity reqEntity = new MultipartEntity();

		for (String[] f : fields) {

			ContentBody cb = null;

			if (f[1].equals(DataTypes.PHOTO)) {
				// TODO: it is very slow on emulator
				File file = new File(f[2]);
				// Log.i(TAG, "image exists: " + file.exists());
				// Log.i(TAG, "image size: " + file.length());
				cb = new FileBody(file, "image/jpeg");
			} else {
				try {
					cb = new StringBody(f[2]);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			reqEntity.addPart(f[0], cb);
			Log.i(TAG, "Key: " + f[0]);
		}

		httppost.setEntity(reqEntity);

		/*
		 * Log.i(TAG, "content-type: " + reqEntity.getContentType()); Log.i(TAG,
		 * "content-encoding: " + reqEntity.getContentEncoding()); Log.i(TAG,
		 * "content-length: " + reqEntity.getContentLength());
		 */

		Log.i(TAG, "Sending observation data to the server");

		HttpResponse response = null;
		try {
			response = getHttpClient().execute(httppost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return NO_CONNECTION;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return FORMAT;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return NO_CONNECTION;
		} catch (Exception e) {
			e.printStackTrace();
			return NO_CONNECTION;
		} finally {
			synchronized (this) {
				httprequest = null;
			}
		}

		String responseText = getResponse(response);

		Log.i(TAG, responseText);

		return REST.getResultFromResponse(responseText);
	}

}
