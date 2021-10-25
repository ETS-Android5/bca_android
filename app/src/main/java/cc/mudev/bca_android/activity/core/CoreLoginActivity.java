package cc.mudev.bca_android.activity.core;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import cc.mudev.bca_android.R;
import cc.mudev.bca_android.activity.main.MainActivity;
import cc.mudev.bca_android.network.APIException;
import cc.mudev.bca_android.network.BCaAPI.AccountAPI;
import cc.mudev.bca_android.network.BCaAPI.ProfileDBSyncAPI;
import cc.mudev.bca_android.util.AlertDialogGenerator;

public class CoreLoginActivity extends AppCompatActivity {
    private static final String ERROR_DIALOG_TITLE = "로그인을 할 수 없어요";
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

        loginBtn.setOnClickListener((view) -> {

            String id = idField.getText().toString().trim(), password = passwordField.getText().toString().trim();
            String errorMessage = null;

            if (id.isEmpty()) errorMessage = "아이디를 입력해주세요";
            else if (password.isEmpty()) errorMessage = "비밀번호를 입력해주세요";

            if (errorMessage != null) {
                AlertDialogGenerator.gen(CoreLoginActivity.this, ERROR_DIALOG_TITLE, errorMessage);
                return;
            }

            try {
                AccountAPI.signin(CoreLoginActivity.this, id, password)
                        .thenComposeAsync((response) -> ProfileDBSyncAPI.updateDB(CoreLoginActivity.this))
                        .thenAcceptAsync((response) -> {
                            Intent mainIntent = new Intent(CoreLoginActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                            finishAffinity();
                        })
                        .exceptionally((e) -> {
                            e.printStackTrace();
                            Throwable originalException = e.getCause();
                            AlertDialogGenerator.gen(
                                    CoreLoginActivity.this,
                                    ERROR_DIALOG_TITLE,
                                    (originalException.getClass() == APIException.class)
                                            ? ((APIException)originalException).displayMsg
                                            : "알 수 없는 문제가 발생했습니다,\n잠시 후 다시 시도해주세요.");
                            return null;
                        });
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialogGenerator.gen(
                        CoreLoginActivity.this,
                        ERROR_DIALOG_TITLE,
                        "알 수 없는 문제가 발생했습니다,\n잠시 후 다시 시도해주세요.");
            }
        });

        registerBtn.setOnClickListener((view) -> {
            Intent registerIntent = new Intent(CoreLoginActivity.this, CoreRegisterActivity.class);
            registerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(registerIntent);
            finish();
        });

        resetMyPasswordBtn.setOnClickListener((view) -> {
            Intent resetMyPasswordIntent = new Intent(CoreLoginActivity.this, CoreResetMyPasswordActivity.class);
            startActivity(resetMyPasswordIntent);
            // Do not finish() here, so that user can return to this page.
        });
    }
}