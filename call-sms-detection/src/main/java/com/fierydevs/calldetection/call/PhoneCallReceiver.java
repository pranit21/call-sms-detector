package com.fierydevs.calldetection.call;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.fierydevs.calldetection.CallSmsDetector;

import java.io.File;
import java.util.Date;

/**
 * Created by Pranit More on 15-04-2017.
 */

public class PhoneCallReceiver extends BroadcastReceiver {

    //The receiver will be recreated whenever android feels like it.
    //We need a static variable to remember data between instantiations
    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;  //because the passed incoming is only valid in ringing
    private static CallListener mCallListener;


    @Override
    public void onReceive(Context context, Intent intent) {
        CallSmsDetector.startOutgoingSms(context);

        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
        } else {
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            int state = 0;
            if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                state = TelephonyManager.CALL_STATE_IDLE;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                state = TelephonyManager.CALL_STATE_RINGING;
            }


            onCallStateChanged(context, state, number);
        }
    }

    //Deals with actual events

    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    public void onCallStateChanged(Context context, int state, String number) {
        if (lastState == state) {
            //No change, debounce extras
            return;
        }
        String contactName = "";
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                savedNumber = number;
                contactName = CallSmsDetector.retrieveContactName(context, number);
                if (mCallListener != null)
                    mCallListener.onIncomingCallStarted(context, number, callStartTime, contactName);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false;
                    callStartTime = new Date();
                    contactName = CallSmsDetector.retrieveContactName(context, savedNumber);
                    CallSmsDetector.startRecording(context);
                    if (mCallListener != null)
                        mCallListener.onOutgoingCallStarted(context, savedNumber, callStartTime, contactName);
                } else {
                    isIncoming = true;
                    callStartTime = new Date();
                    contactName = CallSmsDetector.retrieveContactName(context, savedNumber);
                    CallSmsDetector.startRecording(context);
                    if (mCallListener != null)
                        mCallListener.onIncomingCallAnswered(context, savedNumber, callStartTime, contactName);
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    contactName = CallSmsDetector.retrieveContactName(context, savedNumber);
                    //Ring but no pickup-  a miss
                    CallSmsDetector.stopRecording(); // discard this recording
                    if (mCallListener != null)
                        mCallListener.onMissedCall(context, savedNumber, callStartTime, contactName);
                } else if (isIncoming) {
                    File recordedFile = CallSmsDetector.stopRecording();
                    if (mCallListener != null)
                        mCallListener.onIncomingCallEnded(context, savedNumber, callStartTime, new Date(), recordedFile);
                } else {
                    File recordedFile = CallSmsDetector.stopRecording();
                    if (mCallListener != null)
                        mCallListener.onOutgoingCallEnded(context, savedNumber, callStartTime, new Date(), recordedFile);
                }
                break;
        }
        lastState = state;
    }

    public static void setCallListener(CallListener callListener) {
        mCallListener = callListener;
    }
}
