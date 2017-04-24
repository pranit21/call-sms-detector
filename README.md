# call-sms-detector
This is the library to detect incoming and outgoing calls and incoming and outgoing sms

# Download
```groovy
compile 'com.fierydevs.callsmsdetection:call-sms-detection:0.1.0'
```

# Usage

First ask for permissions using 
```java
CallDetector.askForPermissions(context)
```

Then set listeners using 
```java
SmsReceiver.setSmsReceivedListener(SmsReceivedListener) // to detect received sms
SmsObserver.setSmsSentListener(SmsSentListener) // to detect sent sms
PhoneCallReceiver.setCallListener(CallListener) // to detect calls
```
