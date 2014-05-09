Pivotal CF Mobile Services Push Client SDK for Android
======================================================

Features
--------

The Pivotal CF Mobile Services Push Client SDK is a small tool that will register your application and device with the
Pivotal CF Push Messaging server for receiving push messages.

At this time, this SDK does not provide any code for receiving push messages.

Device Requirements
-------------------

The Push SDK requires API level 10 or greater.

The Google Play Services application must be installed on the device before you can register your device or receive
push messages.  Most devices should already have this application installed, but some odd ones may not.  By default,
Android Virtual Devices (i.e.: emulators) do not have Google Play Services installed.

Instructions for Integrating the Pivotal CF Mobile Push Services Push Client SDK for Android
---------------------------------------------------------------------------------------

In order to receive push messages from Pivotal CF in your Android application you will need to follow these tasks:

 1. Set up a project on Google Cloud Console.  Follow the instructions here:

      http://developer.android.com/google/gcm/gs.html

    You will need obtain the Project Number (AKA the "Sender ID") and register a "Web Application".  The Project
    Number is a parameter you most provide to the Push SDK when registering your device at run-time and to the Pivotal CF
    console.  The Web Application on Google Cloud Console includes an "API Key" that you must supply to the Pivotal CF
    administration console when creating your variant.

 2. Set up your project, application, and a variant on the Pivotal CF administration console.  This task is beyond the scope
    of this document, but please note that you will need the two parameters from Google Cloud Console above.

    After setting up your variant in Pivotal CF, make sure to note the Variant UUID and Variant Secret parameters.  You will
    need them below.

 3. Link the library to your project.  If you are using Gradle to build your project and you have access to the Xtreme
    Labs Maven Repository then you can add the following line to the `dependencies` section of your `build.gradle` file:

        compile 'org.omnia:omnia-pushsdk-android:1.0.0-RELEASE'

        TODO - decide where this library will get published.  It is not likely that the Xtreme Labs Maven Repository
        will still be in use.

 4. Linking the library as an AAR file (as above) should add the appropriate permissions to your `AndroidManifest.xml`
    file.  If you are not able to link the library as an AAR file then you will need to add the following permissions
    manually in order to register your device with Pivotal CF:

        <uses-permission android:name="android.permission.INTERNET"/>
        <uses-permission android:name="android.permission.GET_ACCOUNTS"/>

 5. You will need define and use the following `permission` in the `manifest` element of your application's
    `AndroidManifest.xml` file.  Ensure that the base of the permission name is your application's package name:

        <permission
            android:name="YOUR.PACKAGE.NAME.permission.C2D_MESSAGE"
            android:protectionLevel="signature" />
        <uses-permission android:name="YOUR.PACKAGE.NAME.permission.C2D_MESSAGE" />

 6. You will need to add the following `receiver` to the `application` element of your application's
    `AndroidManifest.xml` file.  Ensure that you set the category name to your application's package name:

        <receiver
            android:name="org.omnia.pushsdk.broadcastreceiver.GcmBroadcastReceiver"
            android:permission="com.google.android.c2dm.permission.SEND">
            <intent-filter>
                <action android:name="com.google.android.c2dm.intent.RECEIVE"/>
                <category android:name="org.omnia.pushsdk.simpledemoapp"/>
            </intent-filter>
        </receiver>

 7. Add the following lines of code to the initialization section of your application.  You will need a `Context` object
    to pass to the `PushLib.init` method, so you should try to add this code to your `Application` class or to one of
    your `Activity` class.

         RegistrationParameters parameters = new RegistrationParameters(gcmSenderId, variantUuid, variantSecret, deviceAlias);
         pushLib = PushLib.init((Context)this);
         pushLib.startRegistration(parameters, null);

    The `gcmSenderId`, `variantUuid`, and `variantSecret` are described above.  The `deviceAlias` is a custom field that
    you can use to differentiate this device from others in your own push messaging campaigns.  You can leave it empty
    if you'd like.

    You should only have to call this method once per the life-time of your application.

    The `startRegistration` method is asynchronous and will return before registration is complete.  If you need to know
    when registration is complete (or if it fails), then provide a `RegistrationListener` as the second argument.

    The Pivotal CF Push SDK takes care of the following tasks for you:
        * Checking for Google Play Services
        * Registering with Google Play Services
        * Saving your registration ID
        * Sending your registration ID to the back-end (i.e.: Pivotal CF)
        * Re-registering after the application version, or any other registration parameters are updated.

 8. To receive push notifications in your application, you will need to add a `broadcast receiver` to your application.
    The intent that GCM sends is provided in the "gcm_intent" parcelable extra of the intent passed to your receiver's
    `onReceive` method.  Here is a simple example:

        public class MyBroadcastReceiver extends WakefulBroadcastReceiver {

            public static final int NOTIFICATION_ID = 1;

            @Override
            public void onReceive(Context context, Intent intent) {

                final Intent gcmIntent = intent.getExtras().getParcelable("gcm_intent");
                final Bundle extras = gcmIntent.getExtras();
                final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
                final String messageType = gcm.getMessageType(gcmIntent);

                if (!extras.isEmpty()) {

                    if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                        // Post notification of received message.
                        String message;
                        if (extras.containsKey("message")) {
                            message = "Received: \"" + extras.getString("message") + "\".";
                        } else {
                            message = "Received message with no extras.";
                        }
                        sendNotification(context, message);
                    }
                }

                MyBroadcastReceiver.completeWakefulIntent(intent);
            }

            // Put the message into a notification and post it.
            private void sendNotification(Context context, String msg) {
                final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                // Set up the notification to open MainActivity when the user touches it
                final PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, MyActivity.class), 0);

                final NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.your icon)
                                .setContentTitle("Your application name")
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                                .setContentText(msg);

                builder.setContentIntent(contentIntent);
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            }
        }

 9. Finally, you will need to declare your broadcast receiver in your `AndroidManifest.xml` file.  The base of the
    action name in the intent filter should be the same as your application's package name:

         <receiver
             android:name=".broadcastreceiver.MyBroadcastReceiver">
             <intent-filter>
                 <action android:name="YOUR.PACKAGE.NAME.omniapushsdk.RECEIVE_PUSH"/>
             </intent-filter>
         </receiver>


Building the SDK itself
-----------------------

You can build this project directly from the command line using Gradle or in Android Studio.

The library depends on the following libraries:

 * Google GSON - should be in the Maven Central repository
 * Google Play Services - should be provided in your Android SDK distribution. If you don't have iton your computer,
                          then run the Android SDK Manager (run `android` from your command line) and install it.  You
                          should be able to find it in the "Extras" section.

To load this project in Android Studio, you will need to select "Import Project" and select the `build.gradle` file in
the project's base directory.

To build the project from the command line, run the command `./gradlew clean assemble`.  If you have a device connected
to your computer then you can also run the unit test suite with the command `./gradlew connectedCheck`.

Staging Server
--------------

At this time, the library is hard coded to use the staging server on Amazon AWS.  You can confirm the current server
by looking at the `BACKEND_REGISTRATION_REQUEST_URL` string value in `Const.java`.  The intent is to change this value
to point to a production server when it is available.

Simple Demo Application
-----------------------

The Simple Demo Application is an example of the simplest application possible that uses the Pivotal CF Push Client SDK.  At
this time, it only demonstrates how to register for push notifications.

This demo application registers for push notifications in the Activity object in order to make it easier to display the
output on the screen.  It is probably more appropriate for you to register for push notifications in your Application
object instead.

This application may be expanded in the future to demonstrate how to receive push notifications but we may need to decide
whether we want to expose the Pivotal CF device ID (which is the "address" that push messages are delivered to).  This information
is not really pertinent to client applications so we might not want to expose it.

Sample Application
------------------

There is a small sample application included in this repository to demonstrate and exercise the features in the Push
Client SDK.

You can use this sample application to test registration against Google Cloud Messaging (GCM) and the Pivotal CF Mobile
Services back-end server for push messages.  Although not currently supported by the library itself, you can also send
and receive push messages with the sample application.

At this time, the sample application uses a dummy project on Google Cloud Console.  It is recommend that you create your
own test Google API Project by following the directions at http://developer.android.com/google/gcm/gs.html.

You can save your own project details by editing the values in the sample project's `push_default_preferences.xml`
resource file.

Watch the log output in the sample application's display to see what the Push library is doing in the background.  This
log output should also be visible in the Android device log (for debug builds), but the sample application registers a
"listener" with the Push Library's logger so it can show you what's going on.

Rotate the display to landscape mode to see the captions for the action bar buttons.

Press the "Register" button in the sample application action bar to ask the Push Library to register the device.  If
the device is not already registered, then you should see a lot of output scroll by as the library registers with
both GCM and Pivotal CF.  If the device is already registered then the output should be shorter.

You can clear all or parts of the saved registration data with the "Clear Registration" action bar option.  Clearing
part or all of the registration data will cause a partial or complete re-registration the next time you press the
"Register" button.

You can change the registration preferences at run-time by selecting the "Edit Registration Parameters" action bar item.
Selecting this item will load the Preferences screen.  There's no space to describe the options on the Preferences screen
itself, but you can look in the `push_default_preferences.xml` file for more details.

You can reset the registration preferences to the default values by selecting the "Reset to Defaults" action bar item in
the Preferences screen.

The sample application (not the library) is also set up to receive push messages once the device has been registered
with GCM and Pivotal CF.  You can choose to push messages directly through GCM or via Pivotal CF.  Although the library does not
support receiving push messages at this time (since the Google framework already provides very straightforward example
code that you can copy into your application), the sample application does as a demonstration to show that the "system
works".  It can be useful for testing your registration set up, or for testing the server itself.
