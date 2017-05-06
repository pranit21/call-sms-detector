package com.fierydevs.calldetection;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;

import com.fierydevs.calldetection.call.CallListener;
import com.fierydevs.calldetection.sms.SmsObserver;
import com.fierydevs.calldetection.sms.SmsReceivedListener;
import com.fierydevs.calldetection.sms.SmsSentListener;

import java.io.File;
import java.io.IOException;

/**
 * Created by Pranit More on 18-04-2017.
 *
 * How to use this class?
 *
 * First ask for permissions using {@link #askForPermissions(Context)}
 * Then set listeners using {@link com.fierydevs.calldetection.sms.SmsReceiver#setSmsReceivedListener(SmsReceivedListener)} to detect received sms,
 * {@link SmsObserver#setSmsSentListener(SmsSentListener)} to detect sent sms,
 * {@link com.fierydevs.calldetection.call.PhoneCallReceiver#setCallListener(CallListener)} to detect calls
 */
public class CallSmsDetector {
    private static MediaRecorder recorder;
    private static boolean recordstarted = false;
    private static File outputFile;

    /**
     * Call this method to start detecting outgoing sms
     *
     * @param context Context of application
     */
    public static void startOutgoingSms(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.registerContentObserver(Uri.parse("content://sms"),true, new SmsObserver(new Handler(), context));
    }

    /**
     * Returns contact name for the provided phone number
     *
     * @param context Context of application
     * @param number phone number received to get contact name
     * @return contactName
     */
    public static String retrieveContactName(Context context, String number) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        String contactName = "";
        if (cursor != null) {
            if(cursor.moveToFirst()) {
                contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            }

            if(!cursor.isClosed()) {
                cursor.close();
            }

            //Log.e(TAG, "Contact Name: " + contactName);
        }
        return contactName;
    }

    /**
     * Check and asks permissions required for this library
     *
     * @param context Context of application
     */
    public static void askForPermissions(Context context) {
        Intent intent = new Intent(context, PermissionsActivity.class);
        context.startActivity(intent);
    }

    /**
     * Checks whether provided service is running or not
     *
     * @param context Context of application
     * @param aClass any {@link android.app.Service} class
     * @return {@literal true|false}
     */
    public static boolean isMyServiceRunning(Context context, Class<?> aClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (aClass.getName().equals(serviceInfo.service.getClassName())) {
                //Log.e("isMyServiceRunning", "yes");
                return true;
            }
        }
        //Log.e("isMyServiceRunning", "no");
        return false;
    }

    public static void startRecording(Context context) {
        //Log.e("recording", "started");
        File audiofile = null;
        File sampleDir = new File(Environment.getExternalStorageDirectory(), "/CallDetectionRecordings");
        if (!sampleDir.exists()) {
            sampleDir.mkdirs();
        }
        String file_name = "Record";
        try {
            audiofile = File.createTempFile(file_name, ".amr", sampleDir);
            outputFile = audiofile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(true);
        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0);

        recorder = new MediaRecorder();
        //recorder.setAudioSource(MediaRecorder.AudioSource.MIC|MediaRecorder.AudioSource.CAMCORDER);

        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION|MediaRecorder.AudioSource.VOICE_DOWNLINK
                |MediaRecorder.AudioSource.VOICE_UPLINK|MediaRecorder.AudioSource.VOICE_CALL|MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(audiofile.getAbsolutePath());
        try {
            recorder.prepare();
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
        recorder.start();
        recordstarted = true;
    }

    public static File stopRecording() {
        if (recordstarted) {
            //Log.e("recording", "recording stopped");
            recorder.stop();
            recordstarted = false;
            return outputFile;
        }
         return null;
    }

    public static void callDeviceAdmin(Context context) {
        Intent intent = new Intent(context, DeviceAdminActivity.class);
        context.startActivity(intent);
    }
}
