package com.example.virginia.cs.edu.politoed;

import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class LightUpRPi extends Activity {

    Button submitBtn;
    EditText urlEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_up_rpi);


        urlEditText = (EditText)findViewById(R.id.enterUrl);
    }



    public void sendRequest(View view) {

        //Get the URL the user entered:
        String url = urlEditText.getText().toString();


        Toast.makeText(getApplicationContext(), "Button clicked", Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(), url , Toast.LENGTH_LONG).show();

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

            arrayElement2.put("lightId", 15);
            arrayElement2.put("red", 0);
            arrayElement2.put("green", 255);
            arrayElement2.put("blue", 0);
            arrayElement2.put("intensity", .7);
            arr.put(arrayElement2);

            arrayElement3.put("lightId", 22);
            arrayElement3.put("red", 0);
            arrayElement3.put("green", 0);
            arrayElement3.put("blue", 255);
            arrayElement3.put("intensity", .7);
            arr.put(arrayElement3);
            json.put("lights", arr);
            json.put("propagate", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sendJson(json, url);
    }

    protected void buildJSONObject() {
        //Get RGB
        //Get Intensity
        //Get lightId
        //add to the JSON object.


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
                        Toast.makeText(getApplicationContext(), in.toString(), Toast.LENGTH_LONG).show();
                    }

                } catch(Exception e) {
                    e.printStackTrace();
                }

                Looper.loop(); //Loop in the message queue
            }
        };

        t.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.light_up_rpi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
