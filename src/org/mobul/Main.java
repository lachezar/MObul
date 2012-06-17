package org.mobul;

import org.mobul.network.Communicator;
import org.mobul.settings.Preferences;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Main extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.main);
		String[] auth = Preferences.getAuthentication(this);
		Communicator.setAuth(auth);
		if (auth.length != 2 || auth[0] == null || auth[1] == null) {
			startActivity(new Intent(this, Login.class));
		} else {
			startActivity(new Intent(this, OEListsHost.class));
		}
		finish();
	}
}