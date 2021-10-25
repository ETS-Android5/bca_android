package cc.mudev.bca_android.activity.core;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import cc.mudev.bca_android.R;
import cc.mudev.bca_android.activity.main.MainActivity;
import cc.mudev.bca_android.network.APIException;
import cc.mudev.bca_android.network.BCaAPI.AccountAPI;
import cc.mudev.bca_android.network.BCaAPI.ProfileDBSyncAPI;
import cc.mudev.bca_android.util.AlertDialogGenerator;

public class CoreRegisterActivity extends AppCompatActivity {
    private static final String ERROR_DIALOG_TITLE = "회원가입을 할 수 없어요";
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

        registerBtn.setOnClickListener((v) -> {
            String email = emailField.getText().toString().trim(),
                    id = idField.getText().toString().trim(),
                    name = nameField.getText().toString().trim(),
                    password = passwordField.getText().toString().trim(),
                    passwordRetype = passwordRetypeField.getText().toString().trim();
            String errorMessage = null;
            if (email.isEmpty()) errorMessage = "이메일을 입력해주세요";
            else if (id.isEmpty()) errorMessage = "아이디를 입력해주세요";
            else if (name.isEmpty()) errorMessage = "성함을 입력해주세요";
            else if (password.isEmpty()) errorMessage = "비밀번호를 입력해주세요";
            else if (passwordRetype.isEmpty()) errorMessage = "비밀번호를 다시 한 번 입력해주세요";
            else if (!password.equals(passwordRetype)) errorMessage = "아래에 다시 입력한 비밀번호가 일치하지 않습니다";

            if (errorMessage != null) {
                AlertDialogGenerator.gen(CoreRegisterActivity.this, ERROR_DIALOG_TITLE, errorMessage);
                return;
            }

            try {
                AccountAPI.signup(CoreRegisterActivity.this, id, password, name, email)
                        .thenComposeAsync((response) -> ProfileDBSyncAPI.updateDB(CoreRegisterActivity.this))
                        .thenAcceptAsync((response) -> {
                            Intent mainIntent = new Intent(CoreRegisterActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                            finishAffinity();
                        })
                        .exceptionally((e) -> {
                            e.printStackTrace();
                            Throwable originalException = e.getCause();
                            AlertDialogGenerator.gen(
                                    CoreRegisterActivity.this,
                                    ERROR_DIALOG_TITLE,
                                    (originalException.getClass() == APIException.class)
                                            ? ((APIException)originalException).displayMsg
                                            : "알 수 없는 문제가 발생했습니다,\n잠시 후 다시 시도해주세요.");
                            return null;
                        });

            } catch (Exception e) {
                e.printStackTrace();
                AlertDialogGenerator.gen(
                        CoreRegisterActivity.this,
                        ERROR_DIALOG_TITLE,
                        "알 수 없는 문제가 발생했습니다,\n잠시 후 다시 시도해주세요.");
            }
        });

        goLoginBtn.setOnClickListener((v) -> {
            Intent loginIntent = new Intent(CoreRegisterActivity.this, CoreLoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginIntent);
            finish();
        });
    }
}