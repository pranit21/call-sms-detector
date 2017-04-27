package com.fierydevs.calldetection.call;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by admin on 25-04-2017.
 */

public class CallDeviceAdmin extends DeviceAdminReceiver {
    public void onEnabled(Context context, Intent intent) {
    }

    public void onDisabled(Context context, Intent intent) {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        /*context.stopService(new Intent(context, TService.class));
        Intent myIntent = new Intent(context, TService.class);
        context.startService(myIntent);*/
    }
}
