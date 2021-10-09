package cc.mudev.bca_android.activity.core;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import cc.mudev.bca_android.R;
import cc.mudev.bca_android.activity.main.MainActivity;
//import cc.mudev.bca_android.networks.Account;
//import cc.mudev.bca_android.networks.NetworkSupport;

public class CoreLoginActivity extends AppCompatActivity {
    public EditText idField;
    public EditText passwordField;
    private Button loginBtn;
    private Button registerBtn;
    private Button resetMyPasswordBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core_login);

        idField = findViewById(R.id.ac_coreLogin_idField);
        passwordField = findViewById(R.id.ac_coreLogin_passwordField);
        loginBtn = findViewById(R.id.ac_coreLogin_loginBtn);
        registerBtn = findViewById(R.id.ac_coreLogin_goRegisterBtn);
        resetMyPasswordBtn = findViewById(R.id.ac_coreLogin_goFindMyPasswordBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(CoreLoginActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finishAffinity();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(CoreLoginActivity.this, CoreRegisterActivity.class);
                registerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(registerIntent);
                finish();
            }
        });

        resetMyPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resetMyPasswordIntent = new Intent(CoreLoginActivity.this, CoreResetMyPasswordActivity.class);
                startActivity(resetMyPasswordIntent);
                // Do not finish() here, so that user can return to this page.
            }
        });
    }
}