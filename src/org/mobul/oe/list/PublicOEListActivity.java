package org.mobul.oe.list;

import org.mobul.OEListsHost;
import org.mobul.R;
import org.mobul.helpers.CommonAsyncTask;
import org.mobul.network.RequestPublicObservationEvents;
import org.mobul.oe.Filter;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.view.View.OnClickListener;

public class PublicOEListActivity extends MyOEListActivity {

	public static final String TAG = "PublicOEListActivity";
	
	public static final int NEW_FILTER_QUERY = 1;
	
	public static final int DIALOG_PROGRESS_ID = 0;
	
	private Button searchButton;
	private String query = "";

	protected String tableName = "public_observation_events";
	
	@Override
	protected String getTableName() {
		return tableName;
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	protected Class getViewClass() {
		return org.mobul.oe.PublicObservationEventView.class;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.public_oe_list);
		
		searchButton = (Button) findViewById(R.id.search_button);
		searchButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivityForResult(new Intent(PublicOEListActivity.this, Filter.class), NEW_FILTER_QUERY);
			}
		});
		
	}

	@Override
	public void init() {
		
		Log.i(TAG, "init()");
		
		try {
			OEListsHost tabHost = (OEListsHost) getParent();
			tabHost.setPublicOEListActivityRef(this);
		} catch (Exception e) {
			e.printStackTrace();
		}

		new CommonAsyncTask(this).execute(new Runnable() {

			private Cursor cursor;
			
			public void run() {
				cursor = dh.getPublicObservationEvent().cursorForOEs(null,
						"id DESC", null, null, null, null, null, true);
				
				runOnUiThread(new Runnable() {
					public void run() {
						SimpleCursorAdapter sca = getCursorAdapter(cursor);
						setListAdapter(sca);
						
						sca.setFilterQueryProvider(new FilterQueryProvider() {
							public Cursor runQuery(CharSequence constraint) {
								Log.i(TAG, "Filter by: " + constraint);
								Cursor c = dh.getPublicObservationEvent().cursorForOEs(null, "id DESC", 
										constraint.toString(), null, null, null, null, false);
								filterConstraint = constraint.toString();
								return c;
							}
						});
						
						ListView lv = getListView();
						
						lv.setTextFilterEnabled(true);
						lv.setFilterText(filterConstraint);
						
					}
					
				});
			}
		});

	}
	
	@Override
	protected SimpleCursorAdapter getCursorAdapter(Cursor cursor) {
		SimpleCursorAdapter sca = new SimpleCursorAdapter(this, R.layout.oe_list_item,
				cursor, new String[] { "title", "participating", 
				"formated_description", "tags", "country" }, 
				new int[] {
				R.id.oe_list_item_title, R.id.oe_list_item_participating, 
				R.id.oe_list_item_description, R.id.oe_list_item_tags,
				R.id.oe_list_item_country});
		return sca;
	}	
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		//super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == NEW_FILTER_QUERY && resultCode == RESULT_OK) {
			Bundle b = data.getExtras();
			query = b.getString("query");
			Log.i(TAG, "Query: " + query);
			new CommonAsyncTask(this).execute(new Runnable() {
				public void run() {
					new RequestPublicObservationEvents().filter(query, dh.getPublicObservationEvent());
					runOnUiThread(new Runnable() {
						
						public void run() {
							// TODO Auto-generated method stub
							init();
						}
					});
				};
			});
			
		}
	}
		
}
