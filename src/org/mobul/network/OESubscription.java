package org.mobul.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;

import android.util.Log;

public class OESubscription extends Communicator {
	
	private static final int SUBSCRIBE = 1;
	private static final int UNSUBSCRIBE = 2;
	
	protected int subscription(int action, String oeId) {
		
		if (!isLoggedIn() && login() != OK) {
			return NO_CONNECTION;
		}
		
		String url = UNSUBSCRIBE_FROM_OE_URL;
		if (action == SUBSCRIBE) {
			url = SUBSCRIBE_TO_OE_URL;
		}
		
		url = url.replace("%OE_ID%", oeId);
		
		Log.i(TAG, "Subscription url: " + url);
		
		HttpPost httppost = getPostRequest(url);
		
		HttpResponse response = null;
		try {
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
		
		int resultCode = REST.getResultFromResponse(responseText);
		
		return resultCode;
		
	}
	
	public int subscribe(String oeId) {
		return subscription(SUBSCRIBE, oeId);
	}
	
	public int unsubscribe(String oeId) {
		return subscription(UNSUBSCRIBE, oeId);
	}
	
	

}
