package com.fierydevs.calldetection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by admin on 25-04-2017.
 */

public class RestartReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.e("RestartReceiver", "broadcast received");
        context.startService(new Intent(context.getApplicationContext(), InfiniteService.class));
    }
}
