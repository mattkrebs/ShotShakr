package com.ratchet.ShotShakr;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.ratchet.ShotShakr.R;
import com.ratchet.ShotShakr.Data.ShotUpdateHelper;

import winterwell.jtwitter.Twitter;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class TwitterActivity extends Activity implements OnClickListener {
	private Button save;
	private TextView username;
	private TextView password;
	private SharedPreferences sPrefs;
	Twitter twitter;

	GoogleAnalyticsTracker tracker;
	static final int PROGRESS_DIALOG = 0;
	static final int SHOW_UPDATE_DIALOG = 1;

	Thread t;
	ProgressDialog progressDialog;
	
	
	
	private static final int DIALOG1_KEY = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.twitter_login);

		tracker = GoogleAnalyticsTracker.getInstance();
		
		tracker.start("UA-16603880-1", this);
		tracker.trackPageView("/twitter");
		
		
		save = (Button) findViewById(R.id.btn_login);

		username = (TextView) findViewById(R.id.txt_username);
		password = (TextView) findViewById(R.id.txt_password);
	

		sPrefs = this.getSharedPreferences("shakr", Context.MODE_PRIVATE);

		username.setText(sPrefs.getString("twitter_user", ""));
		password.setText(sPrefs.getString("twitter_pass", ""));
		
		save.setOnClickListener(this);

	}

	
	
	 @Override
	   protected void onDestroy() {
	     super.onDestroy();
	     // Stop the tracker when it is no longer needed.
	     tracker.stop();
	   }
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case PROGRESS_DIALOG:
			progressDialog = new ProgressDialog(TwitterActivity.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Logging in to twitter...");
			return progressDialog;
			
		case SHOW_UPDATE_DIALOG:
			progressDialog = new ProgressDialog(TwitterActivity.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Downloading the Babbitizers...");
			return progressDialog;
		default:
			return null;
		}
	}

	// Define the Handler that receives messages from the thread and update the
	// progress
	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			String loginmsg = (String) msg.obj;

			if (loginmsg.equals("SUCCESS")) {
				removeDialog(PROGRESS_DIALOG);

				Toast.makeText(TwitterActivity.this,
						"Twitter Account Saved Successfully",
						Toast.LENGTH_SHORT).show();
				SendToActivity(shotpicker.class);
				finish();

			} else {
				password.setText("");
				removeDialog(PROGRESS_DIALOG);
				Log.w("validateTwitter", "Invalid Login");
				Toast.makeText(TwitterActivity.this, "Invalid Login Account",
						Toast.LENGTH_LONG).show();
			}
		}
	};
	
	
	final Handler updatehandler = new Handler() {
		public void handleMessage(Message msg) {
			removeDialog(SHOW_UPDATE_DIALOG);
		}
	};

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		
			Log.w("UPDATING", "Twitter");
			showDialog(PROGRESS_DIALOG);
			t = new Thread() {
				public void run() {
					validateLogin();
				}
			};
			t.start();
			
		
		
	}

	protected void validateLogin() {
		// TODO Auto-generated method stub
		
		
		
		
			if (tryTwitterLogin(username.getText().toString(), password.getText()
					.toString())) {
				Log.w("validateTwitter", "Is Valid Login");
	
				SharedPreferences.Editor e = sPrefs.edit();
				e.putString("twitter_user", username.getText().toString());
				e.putString("twitter_pass", password.getText().toString());
			
	
				e.commit();
				Message myMessage = new Message();
				myMessage.obj = "SUCCESS";
				handler.sendMessage(myMessage);
	
			} else {
	
				SharedPreferences.Editor e = sPrefs.edit();
				e.putString("twitter_user", username.getText().toString());
				e.putString("twitter_pass", password.getText().toString());
				
	
				e.commit();
	
				Message myMessage = new Message();
				myMessage.obj = "FAIL";
				handler.sendMessage(myMessage);
	
			}
		
	}

	public void SendToActivity(Class cls) {
		Intent i = new Intent(getApplicationContext(), cls);
		startActivity(i);
	}

	private Boolean tryTwitterLogin(String username, String password) {
		// TODO Auto-generated method stub
		Log.w("validateTwitter", "Validating Twitter for user...." + username);

		twitter = new Twitter(username, password);
		try {
			return twitter.isValidLogin();
		} catch (Exception e) {
			Log
					.w("TwitterLogin", "Failed To login to twitter"
							+ e.getMessage());

			return false;
		}

	}

}
