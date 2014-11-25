package com.example.virginia.cs.edu.politoed;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;


public class OAuth extends Activity {
    static Twitter twitter;
    static RequestToken requestToken;
    static AccessToken accessToken;
    static SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth);
        if (preferences == null) {
            preferences = PreferenceManager.getDefaultSharedPreferences(this);
        }

        int i = handleCallback();

        if (i == 1 || i == -1) {
            finish();
        } else if (i == 0) {
            authorizeApp();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_oauth, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void authorizeApp() {
        if (twitter == null) {
            ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
            String consumerKey = "LedH6x4GwAKHZeA4gSak5BI0z";
            String consumerSecret = "kpN6DAJfB8tkzG72zfL2FJOV6VqO8gj90lHdUFUXw0a6w7jemQ";

            configurationBuilder.setOAuthConsumerKey(consumerKey);
            configurationBuilder.setOAuthConsumerSecret(consumerSecret);
            Configuration configuration = configurationBuilder.build();

            twitter = new TwitterFactory(configuration).getInstance();
        }

        Thread t = new Thread() {
            public void run() {

                try {
                    twitter.setOAuthAccessToken(null);
                    requestToken = twitter.getOAuthRequestToken("oauth://alarmapp");
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
        try {
            t.join();
        } catch (Exception e) {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(requestToken.getAuthenticationURL()));
        this.startActivity(intent);
    }

    private int handleCallback() {
        Uri uri = getIntent().getData();

        if (uri == null || !uri.toString().startsWith("oauth://alarmapp")) {
            return 0;
        }

        final String verifier = uri.getQueryParameter("oauth_verifier");

        if (verifier == null) {
            finish();
            return -1;
        }

        Thread t = new Thread() {
            public void run() {
                try {
                    accessToken = twitter.getOAuthAccessToken(
                            requestToken, verifier);
                } catch (TwitterException e) {
                }
            }
        };
        t.start();
        try {
            t.join();
        } catch (Exception e) {
            return 0;
        }

        if (accessToken == null) {
            return 0;
        }

        SharedPreferences.Editor e = preferences.edit();
        e.putString("oauth_token", accessToken.getToken());
        e.putString("oauth_token_secret",
                accessToken.getTokenSecret());
        e.putString("twitter_name",
                accessToken.getScreenName());
        e.commit();
        return 1;
    }
}