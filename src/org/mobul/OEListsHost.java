package org.mobul;

import java.lang.ref.WeakReference;
import java.util.List;

import org.mobul.R;
import org.mobul.db.DataHelper;
import org.mobul.db.ObservationData.Model;
import org.mobul.helpers.IFeedbackFromAsyncTask;
import org.mobul.helpers.ProgressBarAsyncTask;
import org.mobul.helpers.ServerSyncAsyncTask;
import org.mobul.network.Communicator;
import org.mobul.network.RequestPublicObservationEvents;
import org.mobul.network.RetrieveObservationEventsCommunicator;
import org.mobul.oe.Filter;
import org.mobul.oe.list.MyOEListActivity;
import org.mobul.oe.list.PublicOEListActivity;
import org.mobul.settings.Preferences;
import org.mobul.utils.StringUtils;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class OEListsHost extends TabActivity implements IFeedbackFromAsyncTask {
	
	private static final String TAG = "OEListsHost";
	
	private TabHost tabHost;
	
	protected Button myOEs;
	protected Button publicOEs;
	protected Button privateOEs;
	protected ImageButton synchronizeOEs;
	protected Button[] tabButtons;
	private static final int[] tabLabels = new int[]{R.string.tab_1, R.string.tab_2, R.string.tab_3};
	protected ViewGroup pendingObservationsCountContainer;
	protected TextView pendingObservationsCount;
	
	private WeakReference<MyOEListActivity> myOEListActivityRef;
	private WeakReference<PublicOEListActivity> publicOEListActivityRef;
	
	private DataHelper dh;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.oes);
		
		buildTabs();
		
		dh = new DataHelper(this);
//		dh.getMyObservationEvent().deleteAll();
//		dh.getPublicObservationEvent().deleteAll();
//		//LoadFixtures.loadDummyObservationEvents(dh.getMyObservationEvent(), 10);
//		LoadFixtures.loadDummyObservationEvents(dh.getPublicObservationEvent(), 5);
		
		pendingObservationsCount = (TextView) findViewById(R.id.pending_count);
		pendingObservationsCountContainer = (ViewGroup) findViewById(R.id.pending_count_container);
		setPendingObservationsCount();
	}
	
	private void buildTabs() {
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
						
		TabSpec myOEsTabSpec = tabHost.newTabSpec("tab1");
		TabSpec publicOEsTabSpec = tabHost.newTabSpec("tab2");
		TabSpec privateOEsTabSpec = tabHost.newTabSpec("tab3");

		myOEsTabSpec.setIndicator("tab1").setContent(new Intent(this, MyOEListActivity.class));
		publicOEsTabSpec.setIndicator("tab2").setContent(new Intent(this, PublicOEListActivity.class));
		privateOEsTabSpec.setIndicator("tab3").setContent(new Intent(this, PublicOEListActivity.class));

		tabHost.addTab(myOEsTabSpec);
		tabHost.addTab(publicOEsTabSpec);
		tabHost.addTab(privateOEsTabSpec);
		
		myOEs = (Button)findViewById(R.id.my_oes_button);
		publicOEs = (Button)findViewById(R.id.public_oes_button);
		privateOEs = (Button)findViewById(R.id.private_oes_button);
		synchronizeOEs = (ImageButton)findViewById(R.id.synchronize_oes_button);
		
		tabButtons = new Button[]{myOEs, publicOEs, privateOEs};
		
		myOEs.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				toggleTabs(0);				
			}
		});
		
		publicOEs.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				toggleTabs(1);				
			}
		});
		
		/*privateOEs.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				toggleTabs(2);				
			}
		});*/
		privateOEs.setVisibility(View.INVISIBLE);
		
		synchronizeOEs.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				synchronize();				
			}
		});
		
		toggleTabs(0);
	}
	
	private void toggleTabs(int index) {
		tabHost.setCurrentTab(index);
		for (Button b : tabButtons) {
			b.setEnabled(true);
		}
		tabButtons[index].setEnabled(false);
		setTitle(tabLabels[index]);
	}
	
	public void setPendingObservationsCount() {
		int count = dh.getObservationData().observationsCount();
		if (count == 0) {
			pendingObservationsCountContainer.setVisibility(View.INVISIBLE);
		} else {
			pendingObservationsCountContainer.setVisibility(View.VISIBLE);
			pendingObservationsCount.setText(String.valueOf(count));
		}
	}
		
	private void synchronize() {
		
		final List<String[]> oes = dh.getMyObservationEvent().selectAllKeyVersion();
		final List<Model> od = dh.getObservationData().selectAllReady();
	
		if (!isWifi()) {
			
			long dataSize = 0;		
			
			for (Model m : od) {
				dataSize += m.dataSize;
			}
			
			dataSize += oes.size()*16;
			
			new AlertDialog.Builder(this).setTitle(R.string.no_wifi)
				.setMessage(getString(R.string.no_wifi_send_data).replace("%DATASIZE%", StringUtils.formatFileSize(dataSize)))
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					
		            public void onClick(DialogInterface dialog, int which) {
		            	syncWithServer(oes, od);
		            }
	
		        }).setNegativeButton(R.string.cancel, null).show();
		} else {
			syncWithServer(oes, od);
		}
	}

	private boolean isWifi() {
		ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean isWifi = (connectivityManager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI);
		return isWifi;
	}
	
	public void sendData(List<Model> od) {
		
		Model[] models = new Model[od.size()];
		new ProgressBarAsyncTask(this, dh.getObservationData()).execute(od.toArray(models)); 
	}
	
	public void syncWithServer(List<String[]> oes, List<Model> od) {
		
		final List<String[]> l = oes;
		new ServerSyncAsyncTask(this, od).execute(new Runnable() {
			public void run() {
				// TODO Auto-generated method stub
				int resultCode = new RetrieveObservationEventsCommunicator().update(l, dh.getMyObservationEvent());
				Log.i(TAG, "Updating the My OEs - " + resultCode);
				
				String[] values = Preferences.getFilter(OEListsHost.this);
				List<String[]> keyValues = Filter.getKeyValues(values);
				String query = StringUtils.generateQueryString(keyValues);
				int publicOEResultCode = new RequestPublicObservationEvents().filter(query, dh.getPublicObservationEvent());
				Log.i(TAG, "Updating the Public OEs - " + publicOEResultCode);
				
				runOnUiThread(new Runnable() {
					
					public void run() {
						
						if (myOEListActivityRef != null && myOEListActivityRef.get() != null) {
							Log.i(TAG, "Updating the My OEs List");
							myOEListActivityRef.get().init();
						} else {
							Log.i(TAG, "myOEListActivityRef.get() is NULL!!!");
						}
						
						if (publicOEListActivityRef != null && publicOEListActivityRef.get() != null) {
							Log.i(TAG, "Updating the Public OEs List");
							publicOEListActivityRef.get().init();
						} else {
							Log.i(TAG, "publicOEListActivityRef.get() is NULL!!!");
						}
					}
				});
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.logout:
	    	Preferences.setAuthentication(this, null, null);
	    	Communicator.resetAuth();
	    	startActivity(new Intent(this, Login.class));
	    	finish();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	public void setMyOEListActivityRef(MyOEListActivity myOEListActivityRef) {
		this.myOEListActivityRef = new WeakReference<MyOEListActivity>(myOEListActivityRef);
	}
	
	public void setPublicOEListActivityRef(PublicOEListActivity publicOEListActivityRef) {
		this.publicOEListActivityRef = new WeakReference<PublicOEListActivity>(publicOEListActivityRef);
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		setPendingObservationsCount();
	}

	public void onPostExecution() {
		setPendingObservationsCount();		
	}
	
}
