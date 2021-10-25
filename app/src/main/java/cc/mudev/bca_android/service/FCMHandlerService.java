package cc.mudev.bca_android.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import cc.mudev.bca_android.R;
import cc.mudev.bca_android.activity.core.CoreSplashActivity;
import cc.mudev.bca_android.dataStorage.SharedPref;
import cc.mudev.bca_android.database.ChatDatabase;
import cc.mudev.bca_android.database.TB_CHAT_EVENT;
import cc.mudev.bca_android.network.BCaAPI.AccountAPI;

public class FCMHandlerService extends FirebaseMessagingService {

    private static final String TAG = "FCMHandlerService";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            try {
                JSONObject fcmBodyData = new JSONObject(remoteMessage.getData());
                ChatDatabase.getInstance(getApplicationContext()).eventDao().insertEvent(new TB_CHAT_EVENT(
                    fcmBodyData.getInt("uuid"),
                    0,
                    fcmBodyData.getString("event_type"),
                    fcmBodyData.getInt("room_id"),
                    fcmBodyData.getString("message"),
                    fcmBodyData.getInt("caused_by_profile_id"),
                    fcmBodyData.getInt("caused_by_participant_id"),
                    fcmBodyData.getString("commit_id"),
                    0, 0, 0
                ));

            } catch (Exception e) { e.printStackTrace(); }

//            if (/* Check if data needs to be processed by long running job */ false) {
//                // For long-running tasks (10 seconds or more) use WorkManager.
//                scheduleJob();
//            } else {
//                // Handle message within 10 seconds
//                handleNow();
//            }

//            sendNotification("데이터", remoteMessage.getData().toString());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(
                    remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }


    /**
     * Called if FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve
     * the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }

    private void scheduleJob() {
        /**
         * Schedule async work using WorkManager.
         */

//        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class)
//                .build();
//        WorkManager.getInstance().beginWith(work).enqueue();
    }
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    private void sendRegistrationToServer(String token) {
        try {
            SharedPref.getInstance(getApplicationContext()).setPref(SharedPref.SharedPrefKeys.FCM, token);
            AccountAPI.isRefreshSuccess(getApplicationContext());
        } catch (Exception e) {
        }
    }

    public void sendNotification(String messageTitle, String messageBody) {
        Intent intent = new Intent(this, CoreSplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0 /* Request code */,
                intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        String channelName = getString(R.string.default_notification_channel_name);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_stat_ic_notification)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    public static String getToken(Context context) {
        String result = context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", null);
        if (result == null || "".equals(result))
            result = getTokenUsingFailsafe(context);
        SharedPref.getInstance(context).setPref(SharedPref.SharedPrefKeys.FCM, result);

        return result;
    }

    private static String getTokenUsingFailsafe(Context context) {
        String sharedPrefFCMToken = SharedPref.getInstance(context).getString(SharedPref.SharedPrefKeys.FCM);
        if (sharedPrefFCMToken == null){
            FirebaseMessaging.getInstance().setAutoInitEnabled(true);
            String result = context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", null);
            FirebaseMessaging.getInstance().setAutoInitEnabled(false);
            return (result != null) ? result : "";
        }
        return sharedPrefFCMToken;
    }
}
