# call-sms-detector
This is the library to detect incoming and outgoing calls and incoming and outgoing sms

# Download
```groovy
compile 'com.fierydevs.callsmsdetection:call-sms-detection:0.2.6'
```

# Usage

First ask for permissions using 
```java
CallSmsDetector.askForPermissions(context);
```

Then add device admin policy so no one can force stop your application
```java
CallSmsDetector.callDeviceAdmin(this);
```

Then create a service class extending InfiniteService class and in onStartCommand method write following code:
```java
public int onStartCommand(Intent intent, int flags, int startId) {
        int ret = super.onStartCommand(intent, flags, startId);
        // to detect received sms
        SmsReceiver.setSmsReceivedListener(new SmsReceivedListener() {
            @Override
            public void onMessageReceived(String number, String contactName, String messageText, long timestamp) {
                Log.e("sms received from", number);
            }
        });
        
        // to detect sent sms
        SmsObserver.setSmsSentListener(new SmsSentListener() {
            @Override
            public void onMessageSent(String number, String contactName, String messageText, long timestamp) {
                Log.e("sms sent to", number);
            }
        });

        // to detect calls
        PhoneCallReceiver.setCallListener(new CallListener() {
            @Override
            public void onIncomingCallStarted(Context ctx, String number, Date start, String contactName) {
                Log.e("incoming started", number);
            }

            @Override
            public void onIncomingCallAnswered(Context ctx, String number, Date start, String contactName) {
                Log.e("incoming answered", number);
            }

            @Override
            public void onOutgoingCallStarted(Context ctx, String number, Date start, String contactName) {
                Log.e("outgoing started", number);
            }

            @Override
            public void onIncomingCallEnded(Context ctx, String number, Date start, Date end, File recordedFile) {
                Log.e("incoming ended", number);
            }

            @Override
            public void onOutgoingCallEnded(Context ctx, String number, Date start, Date end, File recordedFile) {
                Log.e("outgoing ended", number);
            }

            @Override
            public void onMissedCall(Context ctx, String number, Date start, String contactName) {
                Log.e("missed call", number);
            }
        });

        return ret;
    }
```

And lastly start your service.
