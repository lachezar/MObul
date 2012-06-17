package org.mobul.geocoder;

import org.mobul.R;
import org.mobul.geocoder.Geocoder.Address;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class AddressViaLocation {
	
	public static final int ADDRESS_MESSAGE = 1001;
	
	private Context context;
	private LocationManager locationManager;
	private Handler h;
	
	public AddressViaLocation(Context context, Handler h) {
		this.context = context;
		this.h = h;
		
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		
		locationManager.requestLocationUpdates(locationManager.getBestProvider(criteria, true), 2000, 10, locationListener);
		
	}
	
	private final LocationListener locationListener = new LocationListener() {

        public void onLocationChanged(Location location) {
        	if (location != null) {
	    	    ReverseGeocodeLookupTask task = new ReverseGeocodeLookupTask(location, h);
	    		task.applicationContext = context;
	    		task.execute();
	    		locationManager.removeUpdates(locationListener);
        	}
        }

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
        
	};
	

	public class ReverseGeocodeLookupTask extends AsyncTask <Void, Void, Address> {
    	private ProgressDialog dialog;
    	protected Context applicationContext;
    	protected Location location;
    	protected Handler h;
    	
    	public ReverseGeocodeLookupTask(Location location, Handler h) {
    		super();
			this.location = location;
			this.h = h;
		}
    	
    	@Override
    	protected void onPreExecute()
    	{
    		this.dialog = ProgressDialog.show(applicationContext, context.getString(R.string.please_wait), "", true);
    	}
    	
		@Override
		protected Address doInBackground(Void... params) 
		{
			Address address = null;
			
			if (location != null) {
				address = Geocoder.reverseGeocode(location);
			}
			
			return address;
		}
		
		@Override
		protected void onPostExecute(Address result)
		{
			this.dialog.cancel();
			Message m = new Message();
			Bundle b = new Bundle();
			b.putStringArray("address", new String[] {result.city, result.state, result.country, result.countryCode});
			m.setData(b);
			m.what = ADDRESS_MESSAGE;
			h.sendMessage(m);
			//Utilities.showToast("Your Locality is: " + result, applicationContext);
			
		}
    }



}
