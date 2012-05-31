package com.ratchet.ShotShakr;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.ratchet.ShotShakr.R;
import com.ratchet.ShotShakr.Data.Shot;
import com.ratchet.ShotShakr.Data.ShotRepository;



import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;
import android.widget.ViewFlipper;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector;

public class shotpicker extends Activity implements OnTouchListener,
		OnGestureListener {

	private static final int SHAKE_THRESHOLD = 500;
	private ShakeListener mShakr;

	private ViewFlipper mFlipper;
	private ViewFlipper sFlipper;

	private ArrayList<Shot> allShots;
	private TextView shotName;
	private TextView shotName2;

	private TextView shotDetailName;
	private TextView shotDetailIngred;

	private SharedPreferences sPrefs;
	private Twitter twitter;

	private ImageButton btnTookThis;

	private SlidingDrawer filterDrawer;
	
	public ImageView coaster;
	public ImageView coaster2;
	private Typeface fontFace;

	private Boolean homescene;

	private Animation left_out;
	private Animation left_in;
	private Animation right_in;
	private Animation right_out;

	public static final int MENU_TWITTER = 0;
	public static final int MENU_SETTINGS = 1;
	public static final int MENU_FACEBOOK = 2;
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	private Vibrator vibe;
	private boolean isnew = true;
	private boolean isfirst = false;
	private boolean updateTW = false;
	private boolean updateFB = false;
	
	public CheckBox chkVodka;
	public CheckBox chkTequila;
	
	ArrayList<String> filters = new ArrayList<String>();
	
	
    private Facebook mFacebook;
    private AsyncFacebookRunner mAsyncRunner;
	
	private ShotRepository repo;
	private GestureDetector gestureScanner;
	
	GoogleAnalyticsTracker tracker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shot_picker);
		
		tracker = GoogleAnalyticsTracker.getInstance();
		
		tracker.start("UA-16603880-1", this);
		tracker.trackPageView("/shotpicker");
		tracker.dispatch();
		
		Eula.show(this);
		
		sFlipper = ((ViewFlipper) this.findViewById(R.id.background_flipper));

		sPrefs = this.getSharedPreferences("shakr", MODE_PRIVATE);

		// /set Global Animations
		left_out = AnimationUtils.loadAnimation(this, R.anim.push_left_out);
		left_in = AnimationUtils.loadAnimation(this, R.anim.push_left_in);
		right_in = AnimationUtils.loadAnimation(this, R.anim.push_right_in);
		right_out = AnimationUtils.loadAnimation(this, R.anim.push_right_out);
		gestureScanner = new GestureDetector(this);

		mFlipper = ((ViewFlipper) this.findViewById(R.id.flipper));

		mFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_left_in));
		mFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_left_out));

	
		vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		repo = new ShotRepository(this);
	
	
		
		shotName = (TextView) findViewById(R.id.shot);
		shotName2 = (TextView) findViewById(R.id.shot_2);

		shotDetailName = (TextView) findViewById(R.id.shotName);
		shotDetailIngred = (TextView) findViewById(R.id.shotIngredients);

		mFlipper.setOnTouchListener(this);

		fontFace = Typeface.createFromAsset(getAssets(),
				"fonts/homework normal.TTF");

		homescene = true;

		shotName.setTypeface(fontFace);
		shotName.setText("Shake to get this party started!");
		mFacebook = new Facebook();
		
		
		UpdateShots shotUpdater = new UpdateShots(shotpicker.this,null);
	     shotUpdater.execute();
	
		Log.w("Filter Drawer", "Doing Drawer");
		
		View inflatedDrawerLayout = getLayoutInflater().inflate(R.layout.filterdrawer, null);
		int width = getWindow().getAttributes().width, height = getWindow().getAttributes().height;	
		
		LayoutParams params = new LayoutParams(width, height);		
		getWindow().addContentView(inflatedDrawerLayout, params);
				
		XmlResourceParser xrp = this.getResources().getXml(R.xml.filter);
		try{
		while(xrp.getEventType() != XmlResourceParser.END_DOCUMENT){
			if (xrp.getEventType() == XmlResourceParser.START_TAG){
				String s = xrp.getName();
				if (s.equals("filter")){
					int resid = xrp.getAttributeResourceValue(null,"resource", 0);
					String filter = xrp.getAttributeValue(null, "name");
					CheckBox chk = new CheckBox(this);
					chk.setTag(filter);
					chk.setButtonDrawable(resid);
					chk.setWidth(85);
					LinearLayout l = (LinearLayout)inflatedDrawerLayout.findViewById(R.id.filtercontent);
					l.addView(chk);
					chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
						
						@Override
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							// TODO Auto-generated method stub
							if (isChecked){ 
								SetFilter(buttonView.getTag().toString());
							}
							else
								RemoveFilter(buttonView.getTag().toString());
						}
					});
				}
			}
			xrp.next();
		}
		xrp.close();
		}catch(Exception ex){
			
			Log.e("Filter Drawer", "Error loading filters - " + ex.toString());
		}
		/* chkVodka = new CheckBox(this);
		chkTequila = new CheckBox(this);
		
		chkVodka = (CheckBox)inflatedDrawerLayout.findViewById(R.id.vodkabutton);
		chkTequila = (CheckBox)inflatedDrawerLayout.findViewById(R.id.tequilabutton);
		
		chkVodka.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked){ 
					SetFilter("vodka");
				}
				else
					RemoveFilter("vodka");
			}
		});
		
		chkTequila.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked){ 
					SetFilter("tequila");
				}
				else
					RemoveFilter("tequila");
			}
		});
		*/
		
		filterDrawer = (SlidingDrawer)inflatedDrawerLayout.findViewById(R.id.filterSlidingDrawer);
		
		if (filterDrawer != null){
		
		filterDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
				     
				    @Override
				    public void onDrawerClosed() {
				     // TODO Auto-generated method stub
				    	Log.w("SHOT PICKER", "Applying Filters :" + filters.size());
				     UpdateShots shotUpdater = new UpdateShots(shotpicker.this,filters);
				     shotUpdater.execute();
				      
			    }
			  });
		
		}
		mShakr = new ShakeListener(this);
		mShakr.setOnShakeListener(new ShakeListener.OnShakeListener() {

			@Override
			public void onShake() {
				
				if (allShots == null)
					Log.w("SHOT PICKER", "All SHOTS is null");
				
				if (allShots != null && allShots.size() > 0) {
				isnew = false;
					if (homescene) {
						vibe.vibrate(100);
						int rand = (int) (allShots.size() * Math.random());
						
						if (isfirst) {
							shotName.setTypeface(fontFace);
							shotName.setText(allShots.get(rand).shotName);
							isfirst = false;
						} else {
							shotName2.setTypeface(fontFace);
							shotName2.setText(allShots.get(rand).shotName);
							isfirst = true;
						}
						
						mFlipper.showNext();
					}
				} else {
					Toast.makeText(shotpicker.this, "Please Re-open ShotShakr to download latest shots.", Toast.LENGTH_LONG).show();
				}

			}
		});

	}
	
	
	
	public void setShots(ArrayList<Shot> shots){
		allShots = shots;
	}
	public void loadShots()
	{
		allShots = repo.getAllShots();
		repo.close();
	}
	public void SetFilter(String filter){
		filters.add(filter);
	}
	
	public void RemoveFilter(String filter){
		filters.remove(filter);
	}

	public void ShowFirstTimeMsg() {
		Boolean isFirstTime = sPrefs.getBoolean("first_time", true);

		// If first time show instruction Toast
		if ((isFirstTime) && (!homescene)) {
			Toast.makeText(this, "Swipe to the right to go back",
					Toast.LENGTH_LONG).show();
			SharedPreferences.Editor e = sPrefs.edit();
			e.putBoolean("first_time", false);

			e.commit();
		}
	}

	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_TWITTER, 0, "Twitter")
				.setIcon(R.drawable.twitter_icon);
		menu.add(0, MENU_FACEBOOK, 0, "Facebook")
				.setIcon(R.drawable.facebook_icon);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_TWITTER:
			SendToActivty(TwitterActivity.class);
			// overridePendingTransition(R.anim.push_left_in,
			// R.anim.push_left_out);
			return true;
		case MENU_FACEBOOK:
			SendToActivty(FacebookActivity.class);
			// overridePendingTransition(R.anim.push_left_in,
			// R.anim.push_left_out);
			return true;
		}
		return false;

	}

	public void SendToActivty(Class cls) {
		Intent i = new Intent(getApplicationContext(), cls);
		startActivity(i);
	}

	@Override
	public void onResume() {
		mShakr.resume();
		super.onResume();
	}

	@Override
	public void onPause() {
		mShakr.pause();
		super.onPause();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (homescene) {
			if (event.getAction() == MotionEvent.ACTION_UP) {

				sFlipper.setInAnimation(left_in);
				sFlipper.setOutAnimation(left_out);

				if (!homescene)
					return false;

				if (!isnew) {
					if (isfirst) {
						
						SetShotDetails(shotName2.getText().toString());
					} else {
						SetShotDetails(shotName.getText().toString());
					}

					homescene = false;

					sFlipper.showNext();
					ShowFirstTimeMsg();
					return true;
				} else {
					Toast.makeText(this,
							"You must first shake to select a shot!",
							Toast.LENGTH_SHORT).show();
					return false;
				}

			}
		} else {

			return gestureScanner.onTouchEvent(event);
		}
		return true;

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if ((keyCode == KeyEvent.KEYCODE_BACK) && (!homescene)) {
			goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void SetShotDetails(String shotName) {
		Log.w("SelectedShot", "Shot Name = " + shotName);
		Shot myShot = null;
		tracker.trackEvent("Touches", "Touch", shotName , 3);
		tracker.dispatch();
		
		for (Shot shot : allShots) {

			if (shot.shotName.equals(shotName)) {
				myShot = shot;
				break;
			}

		}

		if (myShot != null) {
			shotDetailName.setTypeface(fontFace);
			shotDetailName.setText(myShot.shotName);
			shotDetailIngred.setTypeface(fontFace);
			shotDetailIngred.setText(myShot.amount.replace("||", "\n"));
			
		}
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	 @Override
	   protected void onDestroy() {
	     super.onDestroy();
	     // Stop the tracker when it is no longer needed.
	     tracker.stop();
	   }
	
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		Log.w("FlingVelocity", "Speed: " + velocityX);
		try {
			if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
				return false;

			if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

				if (!homescene) {
					goBack();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return false;
	}

	public void goBack() {
	
		
		sFlipper.setInAnimation(right_in);
		sFlipper.setOutAnimation(right_out);

		sFlipper.showNext();
		homescene = true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return gestureScanner.onTouchEvent(ev);
	}

	@Override
	public void onLongPress(MotionEvent e) {
		if (!homescene) {
			
			vibe.vibrate(100);
			showDialog(1);
		}
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		return new AlertDialog.Builder(shotpicker.this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setMultiChoiceItems(R.array.select_social,
					 new boolean[]{false, false},
                     new DialogInterface.OnMultiChoiceClickListener() {
                         public void onClick(DialogInterface dialog, int whichButton,
                                 boolean isChecked) {

                        	 switch(whichButton){
                        	 case 0:
                        		 updateTW = isChecked;
                        		 break;
                        	 case 1:
                        	 	 updateFB = isChecked;
                        	 	 break;                        	 	 
                        	 }
                             /* User clicked on a check box do some stuff */
                         }
                     })
			.setTitle(R.string.alert_dialog_update_social)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

						if ((!homescene) && (updateTW)) {
							String user = sPrefs.getString("twitter_user", "");
							String pass = sPrefs.getString("twitter_pass", "");
							
							
								twitter = new Twitter(user, pass);

								try {
									// Status to post in Twitter
									twitter.setStatus("I just had a shot called "
											+ shotDetailName.getText()
											+ ", Inspiration provided by @shotshakr for Android.");
									Toast
											.makeText(
													getApplicationContext(),
													"Article Posted to Twitter Successfully!!",
													Toast.LENGTH_SHORT).show();
								} catch (TwitterException.E401 ex) {
									// comes here when username or password is
									// wrongs
									Toast
											.makeText(
													getApplicationContext(),
													"Wrong Username or Password, Kindly Check your login",
													Toast.LENGTH_SHORT).show();
								} catch (Exception ex) {
									Toast.makeText(getApplicationContext(),
											"Network Host not responding",
											Toast.LENGTH_SHORT).show();
								}
						}
						
						if ((!homescene) && (updateFB)){
							
							SessionStore.restore(mFacebook, shotpicker.this);
					       	mAsyncRunner = new AsyncFacebookRunner(mFacebook);
							if (mFacebook.isSessionValid()) 
							{
							Bundle bundle = new Bundle();
			            	bundle.putString("message", "just had a shot called " + shotDetailName.getText() + ". Inspiration provided by ShotShakr for Android.");
			            	bundle.putString("attachment", "{\"name\":\"Add your own shots\",\"href\":\"http://www.shotshakr.com/\",\"caption\":\"www.shotshakr.com\",\"description\":\"Automagicly updates your shotshakr android app\",\"media\":[{\"type\":\"image\",\"src\":\"http://cdn-1.androidzoom.com/logos/57352.png\",\"href\":\"http://www.shotshakr.com\"}]}");
			            	//bundle.putString("attachment", "{\"name\":\"Add your own shot to this app\",\"href\":\"http://www.shotshakr.com/\"\"media\":{\"type\":\"image\",\"src\":\"http://cdn-1.androidzoom.com/logos/57352.png\",\"href\":\"http://www.shotshakr.com/\"}}");
			            	
			                //mFacebook.dialog(Example.this, "stream.publish", 
			                  //      new SampleDialogListener());
			            	
			            	mAsyncRunner.requestPOST("stream.publish",bundle, new WallPostRequestListener());
							} else {
								Toast
								.makeText(
										getApplicationContext(),
										"Please log in to facebook.",
										Toast.LENGTH_SHORT).show();
							}
						}
						
					}
				}).setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						/* User clicked Cancel so do some stuff */
					}
				}).create();
	}
	// Define the Handler that receives messages from the thread and update the
	// progress
	public class WallPostRequestListener extends BaseRequestListener {
    
    public void onComplete(final String response) {
        Log.d("Facebook-Example", "Got response: " + response);
        String message = "<empty>";
        try {
            JSONObject json = Util.parseJson(response);
            message = json.getString("message");
        } catch (JSONException e) {
            Log.w("Facebook OnComplete", "JSON Error in response");
        } catch (FacebookError e) {
            Log.w("Facebook OnComplete", "Facebook Error: " + e.getMessage());
        }
        final String text = "Your Wall Post: " + message;
        shotpicker.this.runOnUiThread(new Runnable() {
            public void run() {
            	Toast
				.makeText(
						getApplicationContext(),
						"Article Posted to Facebook Successfully!!",
						Toast.LENGTH_SHORT).show();
            }
        });
    }
    
   

}

	
	 private class UpdateShots extends AsyncTask<String, Void, ArrayList<Shot>> {

	    	private Context aContext;
	    	private ArrayList<String> aFilters;

	    	public UpdateShots(Context context, ArrayList<String> filters){
	    		aContext = context;
	    		aFilters = filters;
	    	}
	    	

	    	@Override
	    	protected void onPostExecute(ArrayList<Shot> result) {
	    		// TODO Auto-generated method stub
	    		setShots(result);
	    	}


	    	@Override
	    	protected ArrayList<Shot> doInBackground(String... params) {
	    		ShotRepository repo = new ShotRepository(aContext);
	    		Log.w("Update Shots Thread", "Getting Shots and Applying filter");
	    		if (aFilters != null && aFilters.size() > 0){
	    			Log.w("ShotUpdater", "GetShots with Filter");
	    			return repo.getShots(aFilters);
	    		}
	    		else{
	    			Log.w("ShotUpdater", "GetAllShots No Filter");
	    			return repo.getAllShots();}
	    	}
	    	

	    }
	
	

}


