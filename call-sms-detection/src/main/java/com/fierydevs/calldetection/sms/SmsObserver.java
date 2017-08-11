package com.fierydevs.calldetection.sms;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import com.fierydevs.calldetection.CallSmsDetector;

/**
 * Created by Pranit More on 19-04-2017.
 */

public class SmsObserver extends ContentObserver {
    Context context;
    private String lastSmsId;
    private static SmsSentListener mSentListener;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public SmsObserver(Handler handler, Context context) {
        super(handler);
        this.context = context;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        try {
            // save the message to the SD card here
            Uri uriSMSURI = Uri.parse("content://sms/sent");
            Cursor cur = context.getContentResolver().query(uriSMSURI, null, null, null, null);
            // this will make it point to the first record, which is the last SMS sent
            if (cur != null) {
                cur.moveToNext();
                String id = cur.getString(cur.getColumnIndex("_id"));
                if (smsChecker(id)) {
                    String address = cur.getString(cur.getColumnIndex("address"));
                    String content = cur.getString(cur.getColumnIndex("body"));
                    String date = cur.getString(cur.getColumnIndex("date"));
                    //String sim_id = cur.getString(cur.getColumnIndex("sim_id"));
                    /*Log.e("sms", content);
                    Log.e("smsNumber", address);
                    Log.e("date", date);*/
                    //Log.e("sim_id", sim_id);
                    String contactName = CallSmsDetector.retrieveContactName(context, address);

                    mSentListener.onMessageSent(address, contactName, content, Long.parseLong(date));

                /*for (String column: cur.getColumnNames()) {
                    Log.e("cur.getColumnNames()", column);
                }*/
                    cur.close();
                }
            }
        } catch(Exception e) {}
    }

    private boolean smsChecker(String sms) {
        boolean flagSMS = true;

        if (sms.equals(lastSmsId)) {
            flagSMS = false;
        } else {
            lastSmsId = sms;
        }
        //if flagSMS = true, those 2 messages are different
        return flagSMS;
    }

    public static void setSmsSentListener(SmsSentListener smsSentListener) {
        mSentListener = smsSentListener;
    }
}
