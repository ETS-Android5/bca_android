package cc.mudev.bca_android.activity.core;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import cc.mudev.bca_android.R;
import cc.mudev.bca_android.activity.main.MainActivity;
import cc.mudev.bca_android.network.BCaAPI.AccountAPI;
import cc.mudev.bca_android.network.BCaAPI.ProfileDBSyncAPI;
import cc.mudev.bca_android.network.NetworkSupport;
import cc.mudev.bca_android.service.FCMHandlerService;

public class CoreSplashActivity extends AppCompatActivity {
    private long backKeyPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize notification channel, FCM token, and NetworkSupport.
        // This order must be kept, if we initialize NetworkSupport after FCMHandlerService,
        // then we won't get FCM Token forever.
        this.createNotificationChannel();
        FCMHandlerService.getToken(CoreSplashActivity.this);
        NetworkSupport api = NetworkSupport.getInstance(CoreSplashActivity.this);

        // Check if user is signed in.
        // If user is signed in, then send user to MainActivity.
        // If not, then send user to SignIn/SignUp select screen.
        try {
            AccountAPI.isRefreshSuccess(CoreSplashActivity.this).thenAcceptAsync((success) -> {
                if (success) {
                    ProfileDBSyncAPI.updateDB(CoreSplashActivity.this).thenAcceptAsync((t) -> {
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    });
                    return;
                } else if (api.isInternetOffline()) {
                    api.setRoleFromSharedPref();
                    if (api.getCurrentProfileId() != -1) {
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        return;
                    }
                }
                api.resetAuthData();
                Intent intent = new Intent(this, CoreFrontActivity.class);
                startActivity(intent);
                finish();
            });
        } catch (Exception e) {
            e.printStackTrace();
            api.resetAuthData();
            Intent intent = new Intent(this, CoreFrontActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        // 2500 milliseconds = 2.5 seconds
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(CoreSplashActivity.this, "뒤로 가기 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG).show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            finish();
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = getString(R.string.default_notification_channel_id);
            String name = getString(R.string.default_notification_channel_name);
            String description = getString(R.string.default_notification_channel_description);

            NotificationChannel channel = new NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
