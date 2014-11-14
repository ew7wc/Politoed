package com.example.virginia.cs.edu.politoed;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


/**
 * Created by Elaine on 11/12/2014.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Alarm has gone off", Toast.LENGTH_LONG).show();
        Intent i = new Intent(context, AlarmLockScreen.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        context.startActivity(i);
        //Log.e("RECEIVED ALARM", "alarm has gone off");
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
