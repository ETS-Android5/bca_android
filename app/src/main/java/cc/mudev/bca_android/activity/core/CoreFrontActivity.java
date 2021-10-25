package cc.mudev.bca_android.activity.core;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import cc.mudev.bca_android.R;

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

        loginBtn.setOnClickListener((view) -> {
            // Goto Login page
            Intent loginIntent = new Intent(CoreFrontActivity.this, CoreLoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(loginIntent);
        });
        registerBtn.setOnClickListener((view) -> {
            // Goto Register page
            Intent registerIntent = new Intent(CoreFrontActivity.this, CoreRegisterActivity.class);
            registerIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(registerIntent);
        });
    }

    @Override
    public void onBackPressed() {
        // 2500 milliseconds = 2.5 seconds
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG).show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            finish();
        }
    }
}