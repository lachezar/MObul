package org.mobul.oe;

import java.util.LinkedList;
import java.util.List;

import org.mobul.R;
import org.mobul.geocoder.AddressViaLocation;
import org.mobul.helpers.CommonAsyncTask;
import org.mobul.settings.Preferences;
import org.mobul.utils.StringUtils;

import android.app.Activity;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

public class Filter extends Activity {
	
	protected AutoCompleteTextView country;
	protected EditText province;
	protected EditText city;
	protected CheckBox worldwide;
	protected EditText title;
	protected EditText tags;
	protected Spinner order;
	protected ImageButton currentLocation;
	protected LocationManager lm = null;
	protected Button submitButton;
	protected Button resetButton;

	private FilterHandler h;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		h = new FilterHandler();

		setContentView(R.layout.filter);

		country = (AutoCompleteTextView) findViewById(R.id.filter_country);
		province = (EditText) findViewById(R.id.filter_province);
		city = (EditText) findViewById(R.id.filter_city);
		worldwide = (CheckBox) findViewById(R.id.filter_worldwide);
		title = (EditText) findViewById(R.id.filter_title);
		tags = (EditText) findViewById(R.id.filter_tags);
		order = (Spinner) findViewById(R.id.filter_order);
		currentLocation = (ImageButton) findViewById(R.id.filter_current_location);

		String[] countries = getResources().getStringArray(R.array.countries);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, countries);
		country.setAdapter(adapter);

		currentLocation.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				new AddressViaLocation(Filter.this, h);
			}
		});
		
		submitButton = (Button) findViewById(R.id.filter_submit_form);
		submitButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				new CommonAsyncTask(Filter.this).execute(new Runnable() {
					public void run() {
						
						saveFields();
						
						Intent intent = new Intent();
						Bundle b = new Bundle();
						String query = StringUtils.generateQueryString(getKeyValues());
						b.putString("query", query);
						intent.putExtras(b);
						setResult(RESULT_OK, intent);
						
						Filter.this.finish();
					}
				});				
			}
		});
		
		resetButton = (Button) findViewById(R.id.filter_reset_form);
		resetButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				country.setText("");
				province.setText("");
				city.setText("");
				worldwide.setChecked(true);
				title.setText("");
				tags.setText("");
				order.setSelection(0);				
			}
		});
		
		populateFields();

	}
		
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		populateFields();
	}
	
	private void populateFields() {
		String[] filterOptions = Preferences.getFilter(this);
		country.setText(filterOptions[0]);
		province.setText(filterOptions[1]);
		city.setText(filterOptions[2]);
		worldwide.setChecked(Boolean.parseBoolean(filterOptions[3]));
		title.setText(filterOptions[4]);
		tags.setText(filterOptions[5]);
		order.setSelection(Integer.parseInt(filterOptions[6]));
	}
	
	public List<String[]> getKeyValues() {
		
		String[] values = new String[] {
			country.getText().toString(), 
			province.getText().toString(),
			city.getText().toString(),
			String.valueOf(worldwide.isChecked()),
			title.getText().toString(),
			tags.getText().toString(),
			String.valueOf(order.getSelectedItemPosition())
		};
		
		List<String[]> l = getKeyValues(values);
				
		return l;
	}
	
	public static List<String[]> getKeyValues(String[] values) {
		List<String[]> l = new LinkedList<String[]>();
		
		l.add(new String[] {"country", values[0]});
		l.add(new String[] {"province", values[1]});
		l.add(new String[] {"city", values[2]});
		l.add(new String[] {"worldwide", values[3]});
		l.add(new String[] {"title", values[4]});
		l.add(new String[] {"tags", values[5]});
		
		String sortOrder = "DESC";
		if (values[6].equals("1")) {
			sortOrder = "ASC";
		}
		l.add(new String[] {"createdOrder", sortOrder});
		
		return l;
	}
	
	private void saveFields() {
		Preferences.setFilter(this, country.getText().toString(), province.getText().toString(),
				city.getText().toString(), worldwide.isChecked(),
				title.getText().toString(), tags.getText().toString(),
				order.getSelectedItemPosition());
	
	}

	private class FilterHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AddressViaLocation.ADDRESS_MESSAGE:
				String[] address = (String[]) msg.getData().get("address");
				city.setText(address[0]);
				province.setText(address[1]);
				country.setText(address[2] + " - " + address[3]);
				break;

			default:
				break;
			}
		}
	};

}
