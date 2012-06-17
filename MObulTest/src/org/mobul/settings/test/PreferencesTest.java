package org.mobul.settings.test;

import org.mobul.Login;
import org.mobul.settings.Preferences;

import android.test.ActivityInstrumentationTestCase2;

public class PreferencesTest extends ActivityInstrumentationTestCase2<Login> {
	
	public PreferencesTest() {
		super("org.mobul", Login.class);
	}

	public void testAuthentication() {
		Preferences.setAuthentication(getActivity(), null, null);
		String[] auth = Preferences.getAuthentication(getActivity());
		
		assertEquals(2, auth.length);
		assertEquals(null, auth[0]);
		assertEquals(null, auth[1]);
		
		Preferences.setAuthentication(getActivity(), "mail", "pass");
		auth = Preferences.getAuthentication(getActivity());
		
		assertEquals(2, auth.length);
		assertEquals("mail", auth[0]);
		assertEquals("pass", auth[1]);

	}
	
	public void testFilter() {
		Preferences.setFilter(getActivity(), "BG", "Sofia", "Sofia", true, "title here", "tag1, tag2, tag3", 1);
		String[] filter = Preferences.getFilter(getActivity());
		
		assertEquals(7, filter.length);
		assertEquals("BG", filter[0]);
		assertEquals("Sofia", filter[1]);
		assertEquals("Sofia", filter[2]);
		assertEquals("true", filter[3]);
		assertEquals("title here", filter[4]);
		assertEquals("tag1, tag2, tag3", filter[5]);
		assertEquals("1", filter[6]);
		
		Preferences.setFilter(getActivity(), null, null, null, false, null, null, 0);
		filter = Preferences.getFilter(getActivity());
		
		assertEquals(7, filter.length);
		assertEquals("", filter[0]);
		assertEquals("", filter[1]);
		assertEquals("", filter[2]);
		assertEquals("false", filter[3]);
		assertEquals("", filter[4]);
		assertEquals("", filter[5]);
		assertEquals("0", filter[6]);

	}

}
