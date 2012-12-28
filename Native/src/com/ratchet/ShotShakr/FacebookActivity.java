package com.ratchet.ShotShakr;

/*
 * Copyright 2010 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.*;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.ratchet.ShotShakr.SessionEvents.AuthListener;
import com.ratchet.ShotShakr.SessionEvents.LogoutListener;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class FacebookActivity extends Activity {
    
    // Your Facebook Application ID must be set before running this example
    // See http://www.facebook.com/developers/createapp.php
    public static final String APP_ID = "121685344535734";
    
    private static final String[] PERMISSIONS =
        new String[] {"publish_stream"};
    private LoginButton mLoginButton;
    private TextView mText;

	GoogleAnalyticsTracker tracker;
    private Facebook mFacebook;
    private AsyncFacebookRunner mAsyncRunner;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tracker = GoogleAnalyticsTracker.getInstance();
		
		tracker.start("UA-16603880-1", this);
		tracker.trackPageView("/facebook");
		tracker.dispatch();
        if (APP_ID == null) {
            Builder alertBuilder = new Builder(this);
            alertBuilder.setTitle("Warning");
            alertBuilder.setMessage("A Facebook Applicaton ID must be " +
                    "specified before running this example: see Example.java");
            alertBuilder.create().show();
        }
        
        setContentView(R.layout.facebook_login);
        mLoginButton = (LoginButton) findViewById(R.id.login);
       // mText = (TextView) FacebookActivity.this.findViewById(R.id.txt);
     
        
       	mFacebook = new Facebook();
       	mAsyncRunner = new AsyncFacebookRunner(mFacebook);
       
        SessionStore.restore(mFacebook, this);
      //  SessionEvents.addAuthListener(new SampleAuthListener());
       // SessionEvents.addLogoutListener(new SampleLogoutListener());
        mLoginButton.init(mFacebook, PERMISSIONS);
       /* 
        mRequestButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	mAsyncRunner.request("me", new SampleRequestListener());
            }
        });
        mRequestButton.setVisibility(mFacebook.isSessionValid() ?
                View.VISIBLE :
                View.INVISIBLE);
        
        mPostButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Bundle bundle = new Bundle();
            	bundle.putString("message", "This is a test");
            	
                //mFacebook.dialog(Example.this, "stream.publish", 
                  //      new SampleDialogListener());
            	
            	mAsyncRunner.requestPOST("stream.publish",bundle, new WallPostRequestListener());
            }
        });
        mPostButton.setVisibility(mFacebook.isSessionValid() ?
                View.VISIBLE : 
                View.INVISIBLE);
    }
  
    public class SampleAuthListener implements AuthListener {
        
        public void onAuthSucceed() {
            mText.setText("You have logged in! ");
            mRequestButton.setVisibility(View.VISIBLE);
            mPostButton.setVisibility(View.VISIBLE);
        }

        public void onAuthFail(String error) {
            mText.setText("Login Failed: " + error);
        }
    }
    
    public class SampleLogoutListener implements LogoutListener {
        public void onLogoutBegin() {
            mText.setText("Logging out...");
        }
        
        public void onLogoutFinish() {
            mText.setText("You have logged out! ");
            mRequestButton.setVisibility(View.INVISIBLE);
            mPostButton.setVisibility(View.INVISIBLE);
        }
    }
    
    public class SampleRequestListener extends BaseRequestListener {

        public void onComplete(final String response) {
            try {
                // process the response here: executed in background thread
                Log.d("Facebook-Example", "Response: " + response.toString());
                JSONObject json = Util.parseJson(response);
                final String name = json.getString("name");
                
                // then post the processed result back to the UI thread
                // if we do not do this, an runtime exception will be generated
                // e.g. "CalledFromWrongThreadException: Only the original 
                // thread that created a view hierarchy can touch its views."
                FacebookActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        mText.setText("Hello there, " + name + "!");
                    }
                });
            } catch (JSONException e) {
                Log.w("Facebook-Example", "JSON Error in response");
            } catch (FacebookError e) {
                Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
            }
        }
    }
    
    public class WallPostRequestListener extends BaseRequestListener {
        
        public void onComplete(final String response) {
            Log.d("Facebook-Example", "Got response: " + response);
            String message = "<empty>";
            try {
                JSONObject json = Util.parseJson(response);
                message = json.getString("message");
            } catch (JSONException e) {
                Log.w("Facebook-Example", "JSON Error in response");
            } catch (FacebookError e) {
                Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
            }
            final String text = "Your Wall Post: " + message;
            FacebookActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    mText.setText(text);
                }
            });
        }
    }
    
    public class WallPostDeleteListener extends BaseRequestListener {
        
        public void onComplete(final String response) {
            if (response.equals("true")) {
                Log.d("Facebook-Example", "Successfully deleted wall post");
                FacebookActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        mDeleteButton.setVisibility(View.INVISIBLE);
                        mText.setText("Deleted Wall Post");
                    }
                });
            } else {
                Log.d("Facebook-Example", "Could not delete wall post");
            }
        }
    }
    
    public class SampleDialogListener extends BaseDialogListener {

        public void onComplete(Bundle values) {
            final String postId = values.getString("post_id");
            if (postId != null) {
                Log.d("Facebook-Example", "Dialog Success! post_id=" + postId);
                mAsyncRunner.request(postId, new WallPostRequestListener());
                mDeleteButton.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        mAsyncRunner.request(postId, new Bundle(), "DELETE", 
                                new WallPostDeleteListener());
                    }
                });
                mDeleteButton.setVisibility(View.VISIBLE);
            } else {
                Log.d("Facebook-Example", "No wall post made");
            }
        }
    }
    */ 
    }
    @Override
	   protected void onDestroy() {
	     super.onDestroy();
	     // Stop the tracker when it is no longer needed.
	     tracker.stop();
	   }
}