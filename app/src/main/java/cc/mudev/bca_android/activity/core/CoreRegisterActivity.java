package cc.mudev.bca_android.activity.core;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import cc.mudev.bca_android.R;
import cc.mudev.bca_android.activity.main.MainActivity;
//import cc.mudev.bca_android.networks.Account;

public class CoreRegisterActivity extends AppCompatActivity {
    public TextView emailField;
    public TextView idField;
    public TextView nameField;
    public TextView passwordField;
    public TextView passwordRetypeField;

    private Button registerBtn;
    private Button goLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core_register);

        emailField = findViewById(R.id.ac_coreRegister_emailField);
        idField = findViewById(R.id.ac_coreRegister_idField);
        nameField = findViewById(R.id.ac_coreRegister_nameField);
        passwordField = findViewById(R.id.ac_coreRegister_passwordField);
        passwordRetypeField = findViewById(R.id.ac_coreRegister_passwordCheckField);
        registerBtn = findViewById(R.id.ac_coreRegister_registerBtn);
        goLoginBtn = findViewById(R.id.ac_coreRegister_goLoginBtn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Already logged in or Successfully logged in
                Intent mainIntent = new Intent(CoreRegisterActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finishAffinity();
            }
        });

        goLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(CoreRegisterActivity.this, CoreLoginActivity.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(loginIntent);
                finish();
            }
        });
    }
}