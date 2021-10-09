package cc.mudev.bca_alter.activity.core;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import cc.mudev.bca_alter.R;

public class CoreResetMyPasswordActivity extends AppCompatActivity {
    public EditText emailField;
    private Button sendResetMailBtn;
    private Button goBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core_resetmypassword);

        emailField = findViewById(R.id.ac_coreResetMyPassword_emailField);
        sendResetMailBtn = findViewById(R.id.ac_coreResetMyPassword_sendResetMailBtn);
        goBackBtn = findViewById(R.id.ac_coreResetMyPassword_goBackBtn);

        sendResetMailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Do something
            }
        });

        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}