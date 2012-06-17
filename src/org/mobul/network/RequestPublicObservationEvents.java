package org.mobul.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.mobul.db.AbstractObservationEvent.Model;
import org.mobul.db.PublicObservationEvent;
import android.util.Log;

public class RequestPublicObservationEvents extends Communicator {
	
	private static final String TAG = "RequestPublicObservationEvents";
	
	public int filter(String query, PublicObservationEvent poe) {
		
		if (!isLoggedIn() && login() != OK) {
			return NO_CONNECTION;
		}
				
		Log.i(TAG, "Connecting: " + PUBLIC_OES_URL + "?" + query);
		
		HttpGet httpget = getGetRequest(PUBLIC_OES_URL + "?" + query); 
		
		HttpResponse response = null;
		try {
			response = getHttpClient().execute(httpget);
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
		
		poe.deleteAll();
		poe.insert(l);

		return OK;
	}

}
