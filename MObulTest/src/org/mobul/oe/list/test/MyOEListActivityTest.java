package org.mobul.oe.list.test;

import org.mobul.db.DataHelper;
import org.mobul.oe.list.MyOEListActivity;

import com.jayway.android.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;

public class MyOEListActivityTest extends ActivityInstrumentationTestCase2<MyOEListActivity> {
	
	private MyOEListActivity a;
	private Solo solo;

	public MyOEListActivityTest() {
		super("org.mobul", MyOEListActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		
		a = getActivity();
		
		solo = new Solo(getInstrumentation(), a);
		
		DataHelper dh = new DataHelper(a);
		
		dh.getMyObservationEvent().deleteAll();
		dh.getMyObservationEvent().insert("123", "one", "one", "[]", "tag1, tag2", "BG", "", "", 0L, 123);
		dh.getMyObservationEvent().insert("124", "two", "two", "[]", "tag3", "BG", "", "", 0L, 124);
		dh.getMyObservationEvent().insert("125", "three", "three", "[]", "tag1, tag3", "BG", "", "", 0L, 125);
		dh.getMyObservationEvent().insert("126", "four", "four", "[]", "tag2, tag3", "BG", "", "", 0L, 126);
		dh.getMyObservationEvent().insert("127", "five", "five", "[]", "tag2", "BG", "", "", 0L, 127);
		dh.getMyObservationEvent().insert("128", "six", "six", "[]", "tag1", "US", "", "", 0L, 128);
		dh.getMyObservationEvent().insert("129", "seven", "seven", "[]", "", "US", "", "", 0L, 129);
		dh.getMyObservationEvent().insert("130", "eight one", "eight", "[]", "tag1, tag2, tag3", "DE", "", "", 0L, 130);
		dh.getMyObservationEvent().insert("131", "nine", "nine", "[]", "tag4, tag3", "DE", "", "", 0L, 131);
		dh.getMyObservationEvent().insert("132", "ten", "ten", "[]", "tag4, tag3", "GB", "", "", 0L, 132);
	}
	
	public void testInitialDiaplay() {
		assertEquals(10, a.getListAdapter().getCount());
	}
	
	public void testFilterByCountry() {
		a.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				a.getListView().setFilterText("bg");
				
			}
		});
		solo.sleep(500);
		
		assertEquals(5, a.getListAdapter().getCount());
		
	}
	
	public void testFilterByTitle() {
		a.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				a.getListView().setFilterText("one");
				
			}
		});
		solo.sleep(500);
		
		assertEquals(2, a.getListAdapter().getCount());
	}
	
	public void testFilterByTag() {		
		a.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				a.getListView().setFilterText("tag1");
				
			}
		});
		solo.sleep(500);
		
		assertEquals(4, a.getListAdapter().getCount());
	}
	
	

}
