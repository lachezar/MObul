package org.mobul.oe;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mobul.R;
import org.mobul.db.DataHelper;
import org.mobul.db.AbstractObservationEvent;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ObservationEventView extends ListActivity {

	public static final String TAG = "ObservationEventView";

	protected long id;
	protected String tableName;
	protected DataHelper dh;
	protected TextView titleView;
	protected TextView participatingView;
	protected TextView descriptionView;
	protected TextView tagsView;
	protected TextView regionView;
	protected TextView technicalParamsView;
	protected Button oeActionButton;

	protected List<HashMap<String, String>> taskDescriptions;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_list);

		titleView = (TextView) findViewById(R.id.oe_list_item_title);
		participatingView = (TextView) findViewById(R.id.oe_list_item_participating);
		descriptionView = (TextView) findViewById(R.id.oe_list_item_description);
		tagsView = (TextView) findViewById(R.id.oe_list_item_tags);
		regionView = (TextView) findViewById(R.id.oe_list_item_country);
		technicalParamsView = (TextView) findViewById(R.id.oe_list_item_technical_params);
		oeActionButton = (Button) findViewById(R.id.oe_action);

		tableName = getIntent().getExtras().getString("tableName");
		id = getIntent().getExtras().getLong("observation_event_id");

		init();
		populate();

	}
	
	public void init() {
		dh = new DataHelper(ObservationEventView.this);
	}

	public void populate() {
		// Magic object with all data for the OE comes here!
		AbstractObservationEvent.Model m = dh.getObservationEvent(tableName).selectById(id);
		// m.requested_info;

		titleView.setText(m.title);
		descriptionView.setText(m.description);
		//participatingView.setText(TimeUtils.formatDBTimestamp(m.version));
		tagsView.setText(m.tags);
		regionView.setText(m.country);
		technicalParamsView.setText(String.valueOf(m.technicalParams));

		// Populate the list with tasks here!
		taskDescriptions = new LinkedList<HashMap<String, String>>();
		try {
			JSONArray json = new JSONArray(m.observationDataRequest);
			for (int i = 0; i < json.length(); i++) {
				try {
					HashMap<String, String> hm = new HashMap<String, String>();
					JSONObject taskJson = json.getJSONObject(i);
					hm.put("_id", taskJson.getString("id"));
					hm.put("title", taskJson.getString("title"));
					hm.put("description", taskJson.getString("description"));
					hm.put("task", taskJson.toString());
					int count = dh.getObservationData().observationsCountByTaskId(taskJson.getLong("id"));
					String observationsCountMessage = "";
					if (count > 0) {
						observationsCountMessage = count + " " + getString(R.string.pending_observations);
					}
					hm.put("observations_count", observationsCountMessage);
					taskDescriptions.add(hm);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SimpleAdapter sa = new SimpleAdapter(this, taskDescriptions,
				R.layout.task_list_item,
				new String[] { "title", "description", "observations_count" }, new int[] {
						R.id.task_list_item_title,
						R.id.task_list_item_description,
						R.id.task_list_item_observations_count });

		setListAdapter(sa);

	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.i(TAG, "onRestart()");
		populate();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i(TAG, "onResume()");
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);

		Bundle bundle = new Bundle();
		bundle.putString("task", taskDescriptions.get((int) id).get("task"));

		Intent intent = new Intent(this, org.mobul.oe.TaskView.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}
	
}
