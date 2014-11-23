package com.example.virginia.cs.edu.politoed;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * Created by Elaine on 11/12/2014.
 */
public class AlarmReceiver extends BroadcastReceiver {

    DatabaseHelper dh;
    @Override
    public void onReceive(Context context, Intent intent) {
        dh = new DatabaseHelper(context);
        Toast.makeText(context, "Alarm has gone off", Toast.LENGTH_LONG).show();
        Intent i = new Intent(context, AlarmLockScreen.class);

        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

        int alarmID = intent.getIntExtra("alarmID", -1);
        if ((dh.getAlarm(alarmID))!= null) {
            i.putExtra("alarmID",alarmID);
            context.startActivity(i);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }


}
