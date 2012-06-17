package org.mobul.oe.task.test;

import org.mobul.R;
import org.mobul.oe.TaskView;
import org.mobul.oe.task.Field;
import org.mobul.oe.task.GPSField;
import org.mobul.oe.task.TextField;

import com.jayway.android.robotium.solo.Solo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public class FieldsTest extends ActivityInstrumentationTestCase2<TaskView> {
	
	public static final String taskRepr = "{id: \"559\","
		+ "title: \"\","
		+ "description: \"\","
		+ "fields: []}";
	
	public FieldsTest() {
		super("org.mobul", TaskView.class);
	}

	private Activity a; 
	private LayoutInflater inflater;
	private ViewGroup container;
	private Solo solo;
	
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		
		Bundle bundle = new Bundle();
		bundle.putString("task", taskRepr);
		Intent intent = new Intent();
		intent.putExtras(bundle);
		setActivityIntent(intent);
		
		a = getActivity();
		solo = new Solo(getInstrumentation(), a);
		
		solo.sleep(3000);
		
		inflater = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		container = (ViewGroup)a.findViewById(R.id.task_fields);
		
	}
	
	private void addField(final Field f) {
		a.runOnUiThread(new Runnable() {			
			@Override
			public void run() {
				f.appendTo(inflater, container);				
			}
		});
		
		solo.sleep(200);
	}
	
	private void populateField(final Field f, final String repr) {
		a.runOnUiThread(new Runnable() {			
			@Override
			public void run() {
				f.fromString(repr);
				f.populateField();
			}
		});
		solo.sleep(200);
	}
	
	public void testTextField() {
		
		final TextField tf = new TextField(a);
		
		addField(tf);
		
		tf.setType("text");
		tf.setId("text1");
		
		populateField(tf, "[\"text\",\"text1\",\"wow\"]");
		tf.setRequired(true);
		assertTrue(tf.isValid());
		tf.setRequired(false);
		assertTrue(tf.isValid());
		assertEquals("[\"text\",\"text1\",\"wow\"]", tf.toString());
		
		populateField(tf, "[\"text\",\"text1\",\"\"]");
		assertTrue(tf.isValid());		
		assertEquals("[\"text\",\"text1\",\"\"]", tf.toString());
		
		populateField(tf, "[\"text\",\"text1\",\"\"]");
		tf.setRequired(true);
		assertFalse(tf.isValid());		
		assertEquals("[\"text\",\"text1\",\"\"]", tf.toString());
								
		populateField(tf, "[]");
		assertFalse(tf.isValid());		
		assertEquals("[\"text\",\"text1\",\"\"]", tf.toString());

	}
	
	public void testGPSField() {
		
		final GPSField gpsf = new GPSField(a);
		
		addField(gpsf);
		
		gpsf.setType("gps");
		gpsf.setId("gps1");
				
		populateField(gpsf, "[\"gps\",\"gps1\",\"\"]");
		assertTrue(gpsf.isValid());
		assertEquals("[\"gps\",\"gps1\",\"\"]", gpsf.toString());
		
		populateField(gpsf, "[\"gps\",\"gps1\"]");
		assertTrue(gpsf.isValid());
		assertEquals("[\"gps\",\"gps1\",\"\"]", gpsf.toString());
								
		populateField(gpsf, "[]");
		gpsf.setRequired(true);
		assertFalse(gpsf.isValid());		
		assertEquals("[\"gps\",\"gps1\",\"\"]", gpsf.toString());
		
		populateField(gpsf, "[\"gps\",\"gps1\",\"12.3456,45.6789\"]");
		gpsf.setRequired(true);
		assertTrue(gpsf.isValid());
		gpsf.setRequired(false);
		assertTrue(gpsf.isValid());
		assertEquals("[\"gps\",\"gps1\",\"12.3456,45.6789\"]", gpsf.toString());
		
		populateField(gpsf, "[\"gps\",\"gps1\",\"\"]");
		assertTrue(gpsf.isValid());
		assertEquals("[\"gps\",\"gps1\",\"12.3456,45.6789\"]", gpsf.toString());
		
		populateField(gpsf, "[\"gps\",\"gps1\"]");
		assertTrue(gpsf.isValid());
		assertEquals("[\"gps\",\"gps1\",\"12.3456,45.6789\"]", gpsf.toString());
								
		populateField(gpsf, "[]");
		gpsf.setRequired(true);
		assertTrue(gpsf.isValid());		
		assertEquals("[\"gps\",\"gps1\",\"12.3456,45.6789\"]", gpsf.toString());

	}
}
