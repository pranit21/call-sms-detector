# call-sms-detector
This is the library to detect incoming and outgoing calls and incoming and outgoing sms

How to use this class?

First ask for permissions using CallDetector.askForPermissions(context)
Then set listeners using SmsReceiver.setSmsReceivedListener(SmsReceivedListener) to detect received sms,
SmsObserver.setSmsSentListener(SmsSentListener) to detect sent sms,
PhoneCallReceiver.setCallListener(CallListener) to detect calls
