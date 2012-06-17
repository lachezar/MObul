package org.mobul.oe.test;

import java.io.File;

import org.mobul.db.DataHelper;
import org.mobul.helpers.ILocator;
import org.mobul.oe.TaskView;
import org.mobul.oe.task.DateTimeField;
import org.mobul.oe.task.GPSField;
import org.mobul.oe.task.PhotoField;
import org.mobul.utils.ImageUtils;

import com.jayway.android.robotium.solo.Solo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class TaskViewTest extends ActivityInstrumentationTestCase2<TaskView> {
	
	// NOTE!
	// You must upload MObulTest/res/drawable/test.jpg to the /sdcard/DCIM/test.jpg
	// in order to execute the tests correct

	public static final String taskRepr = "{id: \"55\","
			+ "title: \"MObul alpha test\","
			+ "description: \"This is simple demo of Massive Observation client for Android. Please feel free to play with it and send us feedback or comment. Thank you for participating in this project :-) \","
			+ "fields: ["
			+ "{key: \"text1\", type: \"text\", description: \"Type some single-line text\", isMandatory: true},"
			+ "{key: \"note2\", type: \"note\", description: \"Type some multi-line text\", isMandatory: true},"
			+ "{key: \"photo3\", type: \"photo\", description: \"Take a photo\", isMandatory: true},"
			+ "{key: \"geo4\", type: \"gps\", description: \"Take your GPS fix\", isMandatory: true},"
			+ "{key: \"datetime11\", type: \"datetime\", description: \"Take current date and time\", isMandatory: true}"
			+ "]}";

	private TaskView a;
	private ViewGroup taskFields;
	private Solo solo;

	public TaskViewTest() {
		super("org.mobul", TaskView.class);
	}

	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		
		setActivityInitialTouchMode(false);

		Bundle bundle = new Bundle();
		bundle.putString("task", taskRepr);
		Intent intent = new Intent();
		intent.putExtras(bundle);
		setActivityIntent(intent);

		a = getActivity();
		
		/*DataHelper dh = new DataHelper(a);
		LoadFixtures.loadDummyObservationEvents(dh.getMyObservationEvent(), 10);*/
		
		solo = new Solo(getInstrumentation(), a);
		
		solo.sleep(6000);

		taskFields = (ViewGroup) a.findViewById(org.mobul.R.id.task_fields);
	}

	public void testPreConditions() {
		//solo.sleep(3000);
		assertEquals(taskFields.getChildCount(), 5);
	}

	public void testUI() {
		
		fillin("/sdcard/DCIM/test.jpg");
		
		a.finish();
		a = this.getActivity();
				
		EditText text = (EditText) ((ViewGroup) taskFields.getChildAt(0)).getChildAt(1);
		EditText note = (EditText) ((ViewGroup) taskFields.getChildAt(1)).getChildAt(1);
		ImageView img = (ImageView) ((ViewGroup) taskFields.getChildAt(2)).getChildAt(1);
		TextView gps = (TextView) ((ViewGroup) taskFields.getChildAt(3)).getChildAt(1);
		TextView dt = (TextView) ((ViewGroup) taskFields.getChildAt(4)).getChildAt(1);
		assertEquals("test123", text.getText().toString());
		assertEquals("this is \r\n a note", note.getText().toString());
		assertEquals(176, img.getWidth());
		assertEquals("-123°7'24\" : 23°7'24\"", gps.getText().toString());
		assertTrue(dt.getText().toString().length() > 0);		
		
	}
	
	public void testObservationSave() {
		fillin("/sdcard/DCIM/test.jpg");
		
		EditText text = (EditText) ((ViewGroup) taskFields.getChildAt(0)).getChildAt(1);
		EditText note = (EditText) ((ViewGroup) taskFields.getChildAt(1)).getChildAt(1);
		ImageView img = (ImageView) ((ViewGroup) taskFields.getChildAt(2)).getChildAt(1);
		TextView gps = (TextView) ((ViewGroup) taskFields.getChildAt(3)).getChildAt(1);
		TextView dt = (TextView) ((ViewGroup) taskFields.getChildAt(4)).getChildAt(1);
		
		DataHelper dh = new DataHelper(a);
		int count = dh.getObservationData().selectAllReady().size();
		solo.clickOnButton("Save");
		solo.sleep(3000);
		int newCount = dh.getObservationData().selectAllReady().size();
		assertEquals(count + 1, newCount);
		
		assertTrue(a.isFinishing());
		
		a = this.getActivity();
		
		assertEquals("", text.getText().toString());
		assertEquals("", note.getText().toString());
		assertEquals(1, img.getWidth());
		assertEquals("", gps.getText().toString());
		assertTrue(dt.getText().toString().length() == 0);
	}
	
	
	public void testRemovePhoto() {
		Bitmap bmp = ImageUtils.get1mpBitmapFromFile(new File("/sdcard/DCIM/test.jpg"));
		ImageUtils.saveJPG(bmp, new File("/sdcard/DCIM/copy.jpg"));		
		fillin("/sdcard/DCIM/copy.jpg");
		
		assertTrue(new File("/sdcard/DCIM/copy.jpg").exists());
		assertEquals(176, solo.getImage(0).getWidth());
		solo.clickOnImageButton(0);
		
		assertFalse(new File("/sdcard/DCIM/copy.jpg").exists());
		assertEquals(1, solo.getImage(0).getWidth());
	}
	
	private void fillin(String fileName) {
		solo.clearEditText(0);
		solo.enterText(0, "test123");
		assertEquals("test123", solo.getEditText(0).getText().toString());
		
		solo.clearEditText(1);
		solo.enterText(1, "this is \r\n a note");
		assertEquals("this is \r\n a note", solo.getEditText(1).getText().toString());
		
		final PhotoField pf = (PhotoField) a.getFields()[2];
		a.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				pf.reset();
			}
		});
		solo.sleep(1000);
		assertEquals(1, solo.getImage(0).getWidth());
		
		final File f = new File(fileName);		
		a.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				pf.setResult(f);
				
			}
		});
		
		solo.sleep(2000);
		assertEquals(176, solo.getImage(0).getWidth());
		
		solo.clickOnButton(1);
		final GPSField gpsf = (GPSField) a.getFields()[3];
		final ILocator l = gpsf;
		final Location location = new Location("");
		location.setLongitude(-123.12345);
		location.setLatitude(23.12345);
		location.setAccuracy(50);
		
		a.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				gpsf.reset();
			}
		});
		solo.sleep(1000);
		
		assertEquals("", solo.getText(11).getText().toString());
		
		location.setAccuracy(10);
		a.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				l.updateLocation(location);
			}
		});
		assertEquals("-123°7'24\" : 23°7'24\"", solo.getText(11).getText().toString());
		
		final DateTimeField dtf = (DateTimeField) a.getFields()[4];
		a.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				dtf.reset();
			}
		});
		solo.sleep(500);
		assertEquals("", solo.getText(14).getText().toString());
		solo.clickOnButton(2);
		solo.sleep(2000);
		assertTrue(solo.getText(14).getText().toString().length() > 0);

	}
	
	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}

}
