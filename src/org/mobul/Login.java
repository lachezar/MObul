package org.mobul;

import java.util.LinkedList;

import org.mobul.db.DataHelper;
import org.mobul.helpers.CommonAsyncTask;
import org.mobul.network.Communicator;
import org.mobul.network.RetrieveObservationEventsCommunicator;
import org.mobul.settings.Preferences;
import org.mobul.utils.StringUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity {

	protected EditText loginMail;
	protected EditText loginPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		TextView register = (TextView) findViewById(R.id.register_link);
		register.setMovementMethod(LinkMovementMethod.getInstance());
		register.setLinkTextColor(0xFFCCCCCC);

		TextView lostPassword = (TextView) findViewById(R.id.lost_password_link);
		lostPassword.setMovementMethod(LinkMovementMethod.getInstance());
		lostPassword.setLinkTextColor(0xFFCCCCCC);
		
		loginMail = (EditText) findViewById(R.id.login_mail);
		loginPassword = (EditText) findViewById(R.id.login_password);

		Button b = (Button) findViewById(R.id.login_button);
		b.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (StringUtils.isEmpty(loginMail) || StringUtils.isEmpty(loginPassword)) {
					Toast.makeText(Login.this, R.string.empty_email_and_pass, 4000).show();
					return;
				}
				
				final String username = loginMail.getText().toString().trim();
				final String password = loginPassword.getText().toString();//passInSHA1;				
				
				new CommonAsyncTask(Login.this).execute(new Runnable() {
					
					public void run() {
						
						int resultCode = new Communicator().login(username, password);
						
						if (resultCode == Communicator.OK) {
							// save email and pass
							Preferences.setAuthentication(Login.this, username, password);
							
							DataHelper dh = new DataHelper(Login.this);
							
							dh.getMyObservationEvent().deleteAll();
							
							new RetrieveObservationEventsCommunicator().update(new LinkedList<String[]>(), dh.getMyObservationEvent());
							
							startActivity(new Intent(Login.this, OEListsHost.class));
							Login.this.finish();
						} else {
							runOnUiThread(new Runnable() {
								
								public void run() {
									Toast.makeText(Login.this, R.string.incorrect_email_and_pass, 4000).show();
									
								}
							});
							
						}
					}
				});
			}
		});
	}

}
