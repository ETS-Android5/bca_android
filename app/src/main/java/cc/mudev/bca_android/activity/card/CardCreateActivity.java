package cc.mudev.bca_android.activity.card;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import java.util.concurrent.CompletionException;

import cc.mudev.bca_android.R;
import cc.mudev.bca_android.activity.main.MainActivity;
import cc.mudev.bca_android.network.APIException;
import cc.mudev.bca_android.network.BCaAPI.CardAPI;
import cc.mudev.bca_android.network.BCaAPI.ProfileDBSyncAPI;
import cc.mudev.bca_android.network.NetworkSupport;
import cc.mudev.bca_android.util.AlertDialogGenerator;

public class CardCreateActivity extends AppCompatActivity {
    private static final String ERROR_DIALOG_TITLE = "명함을 생성할 수 없어요";
    private static final int PERMISSION_CODE = 0xBABE;
    EditText cardNameField;
    ImageButton cardUploadImageBtn;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_create);

        cardNameField = findViewById(R.id.ac_cardCreate_cardNameField);
        cardUploadImageBtn = findViewById(R.id.ac_cardCreate_cardImageView);

        NetworkSupport api = NetworkSupport.getInstance(CardCreateActivity.this);

        int currentProfileId = api.getCurrentProfileId();
        if (currentProfileId == -1) {
            AlertDialogGenerator.gen(
                    CardCreateActivity.this,
                    "프로필 ID를 가져올 수 없어요.",
                    "현재 프로필의 ID를 가져오는 도중 문제가 발생했습니다.",
                    false,
                    "확인", (DialogInterface dialog, int i) -> {
                        Intent mainActivity = new Intent(CardCreateActivity.this, MainActivity.class);
                        mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainActivity);
                        finish();
                    });
            return;
        }

        Toolbar toolbar = findViewById(R.id.ac_cardCreate_toolbar);
        toolbar.setNavigationOnClickListener((view) -> finish());
        toolbar.setOnMenuItemClickListener((item) -> {
            if (item.getItemId() == R.id.to_cardCreate_saveBtn) {// We need to get card name, but if it's null or blank, abort this.
                String cardName = cardNameField.getText().toString().trim();
                if (cardName.isEmpty()) {  // cardName won't be null.
                    AlertDialogGenerator.gen(
                            CardCreateActivity.this,
                            ERROR_DIALOG_TITLE,
                            "명함의 이름을 입력해주세요");
                    return true;
                } else if (imageUri == null) {
                    AlertDialogGenerator.gen(
                            CardCreateActivity.this,
                            ERROR_DIALOG_TITLE,
                            "명함 사진을 지정해주세요");
                    return true;
                }

                try {
                    CardAPI.create(CardCreateActivity.this, currentProfileId, imageUri, cardName)
                            .thenComposeAsync((response) -> ProfileDBSyncAPI.updateDB(CardCreateActivity.this))
                            .thenAcceptAsync((response) -> {
                                // Restart whole activity as we synced our profile DB
                                Intent mainActivity = new Intent(CardCreateActivity.this, MainActivity.class);
                                mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainActivity);
                                finish();
                            })
                            .exceptionally((e) -> {
                                e.printStackTrace();
                                Throwable originalException = e.getCause();
                                AlertDialogGenerator.gen(
                                        CardCreateActivity.this,
                                        ERROR_DIALOG_TITLE,
                                        (originalException.getClass() == APIException.class)
                                                ? ((APIException) originalException).displayMsg
                                                : "서버에서 명함을 만드는 중 문제가 발생했습니다.");
                                return null;
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                    AlertDialogGenerator.gen(
                            CardCreateActivity.this,
                            ERROR_DIALOG_TITLE, "서버에서 명함을 만드는 중 문제가 발생했습니다.");
                }
            }
            return true;
        });

        ActivityResultLauncher albumActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                (result) -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                        imageUri = result.getData().getData();
                        cardUploadImageBtn.setImageURI(imageUri);
                        return;
                    }
                    Toast.makeText(CardCreateActivity.this, "이미지를 선택해주세요!", Toast.LENGTH_SHORT).show();
                }
        );
        cardUploadImageBtn.setOnClickListener((v) -> {
            try {
                if (ActivityCompat.checkSelfPermission(CardCreateActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CardCreateActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE);

                    if (ActivityCompat.checkSelfPermission(CardCreateActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(CardCreateActivity.this, "명함 사진을 고르시려면 파일 권한을 허용해야 합니다.", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                // Start image choosing activity
                Intent galleryIntent = new Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
                albumActivityLauncher.launch(galleryIntent);
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialogGenerator.gen(CardCreateActivity.this, ERROR_DIALOG_TITLE, "명함 사진을 선택하는 중 문제가 발생했습니다");
            }
        });
    }
}

