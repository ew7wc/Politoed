package com.example.virginia.cs.edu.politoed;

import com.example.virginia.cs.edu.politoed.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class AlarmLockScreen extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    //
    /* put this into your activity class */
    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity
    private int alarmID;

    private DatabaseHelper db;

    private final SensorEventListener mSensorListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent se) {
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta; // perform low-cut filter
            if (mAccel > 6) {
                //Toast toast = Toast.makeText(getApplicationContext(), "Device has been shaken.", Toast.LENGTH_LONG);
                //toast.show();
                // 1. Instantiate an AlertDialog.Builder with its constructor
                /*AlertDialog.Builder builder = new AlertDialog.Builder(AlarmLockScreen.this);

                // 2. Chain together various setter methods to set the dialog characteristics
                builder.setMessage("You shook your device!").setTitle("Device shake detected.");
                // Add the buttons
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        dialog.cancel();
                    }
                });
                // 3. Get the AlertDialog from create()
                AlertDialog dialog = builder.create();
                dialog.show(); */
                stopAlarm();
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_alarm_lock_screen);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.off_button).setOnTouchListener(mDelayHideTouchListener);

        //Sensor set up for shaking
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        Intent i = getIntent();
        alarmID = i.getIntExtra("alarmID", -1);

    }

    public void closeAlarmLockScreen(View v) {
        stopAlarm();
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public void postTweet(String tweet) {
        String token = "2229159438-U33SEv3xfAxLg81vmxankHC8Q2CFGDCtEEWRjwp";
        String tokenSecret = "s5Zr3JN1MAJVq7e6ZQKnQE6Q1csPIriMOtVtNc83udtpA";
        String consumerKey = "LedH6x4GwAKHZeA4gSak5BI0z";
        String consumerSecret = "kpN6DAJfB8tkzG72zfL2FJOV6VqO8gj90lHdUFUXw0a6w7jemQ";

        AccessToken accessToken = new AccessToken(token, tokenSecret);
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);
        twitter.setOAuthAccessToken(accessToken);

        try {
            twitter.updateStatus(tweet);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }
    public void onTweetPressed(final String taskName) {
        Thread t = new Thread() {
            public void run() {
                postTweet(taskName);
            }
        };
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stopAlarm() {
        db = new DatabaseHelper(getApplicationContext());
        Alarm a = db.getAlarm(alarmID);
        Toast.makeText(getApplicationContext(), "Tweeting? "+ alarmID, Toast.LENGTH_LONG).show();
        if (a.getTwitter() == 1) {
            onTweetPressed(a.getName());
        }
        db.deleteAlarm(alarmID);
        finish();
    }

    public void sendRequest(View view) {
        //Code to get the IP address out from settings
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String IP = preferences.getString("example_text", "NA");
        Toast.makeText(getBaseContext(), IP, Toast.LENGTH_LONG)
                .show();
        Toast.makeText(getApplicationContext(), "Button clicked", Toast.LENGTH_LONG).show();


        String url = "http://" + IP + "/rpi";

        Toast.makeText(getApplicationContext(), url, Toast.LENGTH_LONG).show();

        JSONObject json = new JSONObject();
        JSONArray arr = new JSONArray();

        JSONObject arrayElement1 = new JSONObject();
        JSONObject arrayElement2 = new JSONObject();
        JSONObject arrayElement3 = new JSONObject();
        try {
            arrayElement1.put("lightId", 0);
            arrayElement1.put("red", 255);
            arrayElement1.put("green", 0);
            arrayElement1.put("blue", 0);
            arrayElement1.put("intensity", .3);
            arr.put(arrayElement1);

            arrayElement2.put("lightId", 2);
            arrayElement2.put("red", 0);
            arrayElement2.put("green", 255);
            arrayElement2.put("blue", 0);
            arrayElement2.put("intensity", .3);
            arr.put(arrayElement2);

            arrayElement3.put("lightId", 22);
            arrayElement3.put("red", 0);
            arrayElement3.put("green", 0);
            arrayElement3.put("blue", 255);
            arrayElement3.put("intensity", .3);
            arr.put(arrayElement3);
            json.put("lights", arr);
            json.put("propagate", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sendJson(json, url);
    }

    protected void sendJson(final JSONObject json, final String url) {
        Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread
                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;

                try {
                    HttpPost post = new HttpPost(url);
                    StringEntity se = new StringEntity(json.toString());
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    post.setEntity(se);

                    response = client.execute(post);

                    /*Checking response */
                    if(response!=null){
                        InputStream in = response.getEntity().getContent(); //Get the data in the entity
                        Toast.makeText(getApplicationContext(), "Request sent!", Toast.LENGTH_LONG).show();
                    }

                } catch(Exception e) {
                    e.printStackTrace();

                    Log.e("Error", "Cannot Establish Connection");
                }

                Looper.loop(); //Loop in the message queue
            }
        };

        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
