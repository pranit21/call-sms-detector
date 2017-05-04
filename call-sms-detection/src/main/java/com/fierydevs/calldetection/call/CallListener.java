package com.fierydevs.calldetection.call;

import android.content.Context;

import java.io.File;
import java.util.Date;

/**
 * Created by Pranit More on 19-04-2017.
 */

public interface CallListener {
    void onIncomingCallStarted(Context ctx, String number, Date start, String contactName);
    void onIncomingCallAnswered(Context ctx, String number, Date start, String contactName);
    void onOutgoingCallStarted(Context ctx, String number, Date start, String contactName);
    void onIncomingCallEnded(Context ctx, String number, Date start, Date end, File recordedFile);
    void onOutgoingCallEnded(Context ctx, String number, Date start, Date end, File recordedFile);
    void onMissedCall(Context ctx, String number, Date start, String contactName);
}
