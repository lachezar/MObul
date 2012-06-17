package org.mobul.oe.list;

import org.mobul.OEListsHost;
import org.mobul.R;
import org.mobul.db.DataHelper;
import org.mobul.helpers.CommonAsyncTask;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MyOEListActivity extends ListActivity {

	public static final String TAG = "MyOEListActivity";

	public static final int DIALOG_PROGRESS_ID = 0;
	
	protected DataHelper dh;
	protected String tableName = "my_observation_events";
	
	protected String filterConstraint = "";
	
	protected String getTableName() {
		return tableName;
	}
	
	@SuppressWarnings("rawtypes")
	protected Class getViewClass() {
		return org.mobul.oe.MyObservationEventView.class;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.oe_list);
		
		dh = new DataHelper(this);
		init();		
	}
	
	public void init() {
		
		Log.i(TAG, "init()");
		
		try {
			OEListsHost tabHost = (OEListsHost) getParent();
			tabHost.setMyOEListActivityRef(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		new CommonAsyncTask(this).execute(new Runnable() {
			
			private Cursor cursor;
			
			public void run() {
				cursor = dh.getMyObservationEvent().cursorForOEs(null, "id DESC",
						null, null, null, null, null, true);
				runOnUiThread(new Runnable() {
					public void run() {
						SimpleCursorAdapter sca = getCursorAdapter(cursor);
						setListAdapter(sca);

						sca.setFilterQueryProvider(new FilterQueryProvider() {
							public Cursor runQuery(CharSequence constraint) {
								Log.i(TAG, "Filter by: " + constraint);
								Cursor c = dh.getMyObservationEvent().cursorForOEsByKeyword(constraint.toString(), null, "id DESC");
								filterConstraint = constraint.toString();
								return c;
							}
						});
						
						getListView().setTextFilterEnabled(true);
						getListView().setFilterText(filterConstraint);
						getListView().requestFocus();
						getListView().clearFocus();
					}
				});
			}
		});
		
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);

		Bundle bundle = new Bundle();
		bundle.putLong("observation_event_id", id);
		bundle.putString("tableName", getTableName());

		Intent intent = new Intent(this, getViewClass());
		intent.putExtras(bundle);
		startActivity(intent);
	}
	
	protected SimpleCursorAdapter getCursorAdapter(Cursor cursor) {
		SimpleCursorAdapter sca = new SimpleCursorAdapter(this, R.layout.oe_list_item,
				cursor, new String[] { "title", 
				"formated_description", "tags", "country" }, 
				new int[] {
				R.id.oe_list_item_title, 
				R.id.oe_list_item_description, R.id.oe_list_item_tags,
				R.id.oe_list_item_country});
		return sca;
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		init();
		Log.i(TAG, "onRestart");
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//init();
		Log.i(TAG, "onResume");
	}
		
}
