package org.mobul.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.mobul.db.AbstractObservationEvent.Model;
import org.mobul.db.MyObservationEvent;

import android.util.Log;

public class RetrieveObservationEventsCommunicator extends Communicator {
	
	public int update(List<String[]> oes, MyObservationEvent myoe) {
		
		if (!isLoggedIn() && login() != OK) {
			return NO_CONNECTION;
		}
		
		HttpPost httppost = getPostRequest(UPDATE_OES_URL);
		
		HttpResponse response = null;
		try {
			UrlEncodedFormEntity uefe = generateQuery(oes);
			httppost.setEntity(uefe);
			response = getHttpClient().execute(httppost);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return FORMAT;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return NO_CONNECTION;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return NO_CONNECTION;
		}  
		
		String responseText = getResponse(response);
		
		Log.i(TAG, responseText);
		
		List<Model> l = REST.getOEs(responseText);
		
		myoe.replace(l);

		return OK;
	}

}
