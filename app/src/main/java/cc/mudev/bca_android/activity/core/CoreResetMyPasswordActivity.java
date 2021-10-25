package cc.mudev.bca_android.activity.core;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import cc.mudev.bca_android.R;
import cc.mudev.bca_android.network.APIException;
import cc.mudev.bca_android.network.BCaAPI.AccountAPI;
import cc.mudev.bca_android.util.AlertDialogGenerator;

public class CoreResetMyPasswordActivity extends AppCompatActivity {
    private static final String ERROR_DIALOG_TITLE = "비밀번호 초기화 메일을 보낼 수 없어요";
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

        sendResetMailBtn.setOnClickListener((view) -> {
            String email = emailField.getText().toString().trim();
            if (email.isEmpty()) {
                AlertDialogGenerator.gen(
                        CoreResetMyPasswordActivity.this,
                        ERROR_DIALOG_TITLE,
                        "회원가입 시 입력했던 메일 주소를 입력해주세요");
                return;
            }

            try {
                AccountAPI.sendPasswordResetMail(CoreResetMyPasswordActivity.this, email)
                        .thenAcceptAsync((response) -> {
                            AlertDialogGenerator.gen(
                                    CoreResetMyPasswordActivity.this,
                                    ERROR_DIALOG_TITLE,
                                    "입력하신 메일 주소로 해당 계정의 비밀번호를 초기화할 수 있는 메일을 보냈습니다.");
                            finish();
                        })
                        .exceptionally((e) -> {
                            e.printStackTrace();
                            Throwable originalException = e.getCause();
                            AlertDialogGenerator.gen(
                                    CoreResetMyPasswordActivity.this,
                                    ERROR_DIALOG_TITLE,
                                    (originalException.getClass() == APIException.class)
                                            ? ((APIException)originalException).displayMsg
                                            : "알 수 없는 문제가 발생했습니다,\n잠시 후 다시 시도해주세요.");
                            return null;
                        });
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialogGenerator.gen(
                        CoreResetMyPasswordActivity.this,
                        ERROR_DIALOG_TITLE,
                        "알 수 없는 문제가 발생했습니다,\n잠시 후 다시 시도해주세요.");
            }
        });

        goBackBtn.setOnClickListener((view) -> finish());
    }
}