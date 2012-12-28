package com.ratchet.ShotShakr;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Startup extends Activity {
	
	 GoogleAnalyticsTracker tracker;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		tracker = GoogleAnalyticsTracker.getInstance();
		
		tracker.start("UA-16603880-1", this);
		tracker.trackPageView("/Startup");
		
		Boolean eulaaproved = Eula.show(this);
		
		if (eulaaproved)
			SendToActivity(ShotSplash.class);
		
	}
	
	public void SendToActivity(Class cls) {
		tracker.dispatch();
		Intent i = new Intent(getApplicationContext(), cls);
		startActivity(i);
	}
}
