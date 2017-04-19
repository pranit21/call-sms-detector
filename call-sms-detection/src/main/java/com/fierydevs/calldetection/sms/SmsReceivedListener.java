package com.fierydevs.calldetection.sms;

/**
 * Created by Pranit More on 19-04-2017.
 */

public interface SmsReceivedListener {
    void onMessageReceived(String number, String contactName, String messageText, long timestamp);
}
