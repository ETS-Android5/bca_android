package cc.mudev.bca_alter.activity.core;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import cc.mudev.bca_alter.R;
//import cc.mudev.bca_alter.networks.Account;
//import cc.mudev.bca_alter.networks.NetworkSupport;
import cc.mudev.bca_alter.util.DebouncedOnClickListener;

public class CoreFrontActivity extends AppCompatActivity {
    private Button loginBtn;
    private Button registerBtn;

    private long backKeyPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core_front);

        // TODO: support dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        loginBtn = findViewById(R.id.ac_coreFront_goLoginBtn);
        registerBtn = findViewById(R.id.ac_coreFront_goRegisterBtn);

        // Check if user already signed in and has a valid account information
//        NetworkSupport api = NetworkSupport.getInstance();
//        api.initialize(this);
//        if (Account.isRefreshSuccess()) {
//            // User is already signed in
//            Intent profileListIntent = new Intent(this, CardActivity.class);
//            profileListIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(profileListIntent);
//            finish();
//        }

        loginBtn.setOnClickListener(new DebouncedOnClickListener() {
            @Override
            public void onDebouncedClick(View view) {
                // Goto Login page
                Intent loginIntent = new Intent(view.getContext(), CoreLoginActivity.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                view.getContext().startActivity(loginIntent);
            }
        });
        registerBtn.setOnClickListener(new DebouncedOnClickListener() {
            @Override
            public void onDebouncedClick(View view) {
                // Goto Register page
                Intent registerIntent = new Intent(view.getContext(), CoreRegisterActivity.class);
                registerIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                view.getContext().startActivity(registerIntent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // 2500 milliseconds = 2.5 seconds
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast toast = Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            finish();
        }
    }
}