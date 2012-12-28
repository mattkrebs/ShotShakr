package com.ratchet.ShotShakr;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ratchet.ShotShakr.R;

import com.ratchet.ShotShakr.Data.FilterUpdateHelper;
import com.ratchet.ShotShakr.Data.Shot;
import com.ratchet.ShotShakr.Data.ShotRepository;
import com.ratchet.ShotShakr.Data.ShotUpdateHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

public class ShotSplash extends Activity implements OnTouchListener {

	protected boolean _active = true;
	protected int _splashTime = 5000;

	// date and time
	private int mYear;
	private int mMonth;
	private int mDay;
	private int mHour;
	private int mMinute;

	private int max_records;

	private ProgressDialog mProgressDialog;
	private ProgressBar mProgressBar;
	private int mProgress;
	private Handler mProgressHandler;

	static final int SHOW_AGE_DIALOG = 0;
	static final int SHOW_UPDATE_DIALOG = 1;
	private Thread mSplashThread;

	private SharedPreferences sPrefs;

	GoogleAnalyticsTracker tracker;
	private ShotRepository dataSource;
	ShotUpdateHelper uH;
	FilterUpdateHelper fUH = new FilterUpdateHelper();

	// splash
	// thread for displaying the SplashScreen
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		tracker = GoogleAnalyticsTracker.getInstance();
		Log.w("SHOT SPLASH", "Created");

		tracker.start("UA-16603880-1", this);
		tracker.trackPageView("/shotsplash");
		sPrefs = this.getSharedPreferences("shakr", MODE_PRIVATE);
		dataSource = new ShotRepository(this);
		dataSource.open();
		
		//this.uH = new ShotUpdateHelper(this);
		//this.fUH = new FilterUpdateHelper();
		showSplash();
		
	
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// Stop the tracker when it is no longer needed.
		tracker.stop();
	}

	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			removeDialog(SHOW_UPDATE_DIALOG);
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case SHOW_AGE_DIALOG:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		case SHOW_UPDATE_DIALOG:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(
					"There are new shots avalable. Would you like to update?")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									new DownloadShots().execute();
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			AlertDialog alert = builder.create();

			return alert;

			// mProgressDialog = new ProgressDialog(this);
			// mProgressDialog.setIcon(android.R.drawable.ic_dialog_alert);
			// mProgressDialog.setTitle(R.string.update_dialog);
			// mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

			// return mProgressDialog;
		}
		return null;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case SHOW_AGE_DIALOG:
			Calendar cal = Calendar.getInstance();
			DatePickerDialog dlg = (DatePickerDialog) dialog;

			dlg.updateDate(1991, 9, 27);
			dlg.setTitle(R.string.age_verify);

			break;
		case SHOW_UPDATE_DIALOG:
			break;
		}
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

			try {

				Date yourAge = df
						.parse(mYear + "-" + mMonth + "-" + dayOfMonth);

				Date legAge = df.parse((cal.get(Calendar.YEAR) - 21) + "-"
						+ cal.get(Calendar.MONTH) + "-"
						+ cal.get(Calendar.DATE));

				tracker.trackEvent("Age", "OK_BUTTON", yourAge.toString(), 1);
				tracker.dispatch();
				Log.w("Ages", yourAge.toString() + " - " + legAge.toString());
				if (yourAge.equals(legAge) || yourAge.before(legAge)) {
					SharedPreferences.Editor e = sPrefs.edit();
					e.putBoolean("ValidAge", true);
					e.commit();
					showSplash();
				} else {
					Toast.makeText(ShotSplash.this,
							"Come back when you are older.", Toast.LENGTH_LONG)
							.show();
				}

			} catch (ParseException e) {
				Log.w("DateChecker", "Could not parse date");
			}
		}
	};

	public void showSplash() {
		if (dataSource.getAllShots().size() == 0)
			new DownloadShots().execute();
		else
			SendToActivity(shotpicker.class);
		//if (uH.hasUpdate()) {
		//	showDialog(SHOW_UPDATE_DIALOG);
		//} else {

	//		
		//}
		// Moved To dialog
		// new DownloadShots().execute();

	}

	public void SendToActivity(Class cls) {
		tracker.dispatch();
		Intent i = new Intent(getApplicationContext(), cls);
		startActivity(i);
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent evt) {

		showSplash();
		return true;

	}

	private class DownloadShots extends AsyncTask<Void, Integer, Void> {
		@Override
		 protected void onProgressUpdate(Integer...progress) {
			
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			try{
				Log.i("Shot Updater", "UpdatingShots");
				
				ShotRepository dataSource = new ShotRepository(getApplicationContext());
				dataSource.open();
				
				
				InputStream is = getResources().openRawResource(R.raw.shot_json);
				Gson gson = new Gson();
				Reader reader = new InputStreamReader(is);
				JsonParser parser = new JsonParser();
				JsonArray jArray = parser.parse(reader).getAsJsonArray();
					
				ArrayList<Shot> shots = new ArrayList<Shot>();
				
				for(JsonElement obj : jArray){
					Shot shot = gson.fromJson(obj, Shot.class);
					dataSource.shotInsert(shot);
					shots.add(shot);
					
				}
				
				dataSource.close();
				
			}catch (Exception ex){
				ex.printStackTrace();
			}
		
			//uH.GetShotUpdate(ShotSplash.this);
			//fUH.GetFilterUpdate(ShotSplash.this);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// removeDialog(SHOW_UPDATE_DIALOG);
			Log.w("SHOT SPLASH", "Sending to Shot Picker Activity");
			SendToActivity(shotpicker.class);
		}

	}

}