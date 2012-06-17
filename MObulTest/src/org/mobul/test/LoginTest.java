package org.mobul.test;

import org.mobul.Login;
import org.mobul.settings.Preferences;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

// functional

public class LoginTest extends ActivityInstrumentationTestCase2<Login> {
	
	private Solo solo;
	
	public LoginTest() {
		super("org.mobul", Login.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		
		solo = new Solo(getInstrumentation(), getActivity());
		
		Preferences.setAuthentication(getActivity(), null, null);
	}
	
	public void testValidation() {
	
		solo.clickOnButton(0);
		solo.sleep(2200);
		
		assertFalse(getActivity().isFinishing());
		
		solo.enterText(0, "lucho@mob.bg");
		solo.clickOnButton(0);
		solo.sleep(2200);
		
		assertFalse(getActivity().isFinishing());
		
		solo.enterText(1, "lucho@mob.bg");
		solo.clickOnButton(0);
		solo.sleep(3000);
				
		assertTrue(getActivity().isFinishing());
	}
	
	public void testAuthSave() {
		String[] saved = Preferences.getAuthentication(getActivity());
		assertEquals(null, saved[0]);
		assertEquals(null, saved[1]);
		
		solo.enterText(0, "lucho@mob.bg");
		solo.enterText(1, "lucho@mob.bg");
		solo.clickOnButton(0);
		
		solo.sleep(10000);
		
		String[] newSaved = Preferences.getAuthentication(getActivity());
		assertEquals("lucho@mob.bg", newSaved[0]);
		assertEquals("lucho@mob.bg", newSaved[1]);
	}
	
	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}

}
