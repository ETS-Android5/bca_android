package cc.mudev.bca_android.activity.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

import cc.mudev.bca_android.R;
import cc.mudev.bca_android.activity.main.MainActivity;
import cc.mudev.bca_android.adapter.ProfileCreateParentAdapter;
import cc.mudev.bca_android.adapter.ProfileCreateParentData;
import cc.mudev.bca_android.adapter.ProfileCreateParentItemTouchHelperCallback;
import cc.mudev.bca_android.network.APIException;
import cc.mudev.bca_android.network.BCaAPI.ProfileAPI;
import cc.mudev.bca_android.util.AlertDialogGenerator;

public class ProfileCreateActivity extends AppCompatActivity {
    private static final String ERROR_DIALOG_TITLE = "프로필을 만들 수 없어요";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_create);

        RecyclerView recyclerView = findViewById(R.id.ac_profileCreate_recyclerView);
        ProfileCreateParentAdapter profileCreateParentAdapter = new ProfileCreateParentAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(profileCreateParentAdapter);

        ItemTouchHelper profileCreateParentItemTouchHelper = new ItemTouchHelper(new ProfileCreateParentItemTouchHelperCallback(profileCreateParentAdapter));
        profileCreateParentItemTouchHelper.attachToRecyclerView(recyclerView);
        profileCreateParentAdapter.itemTouchHelper = profileCreateParentItemTouchHelper;

        EditText profileNameField = findViewById(R.id.ac_profileCreate_nameField);
        EditText profileDescriptionField = findViewById(R.id.ac_profileCreate_descriptionField);

        Button addFieldGroupBtn = findViewById(R.id.ac_profileCreate_itemAddBtn);
        addFieldGroupBtn.setOnClickListener((v) -> {
            // We need to filter already added group name
            String[] addFieldGroupDefaultItems = getResources().getStringArray(R.array.sa_profileCreate_addGroupDialogItem);
            ArrayList<String> addFieldGroupItems = new ArrayList(Arrays.asList(addFieldGroupDefaultItems));
            for (ProfileCreateParentData prpData : profileCreateParentAdapter.profileCreateParentDataList) {
                addFieldGroupItems.remove(prpData.childDataName);
            }
            String[] addFieldGroupItemsArray = new String[addFieldGroupItems.size()];
            int addFieldGroupItemsArrayIndex = 0;
            for (String item : addFieldGroupItems) {
                addFieldGroupItemsArray[addFieldGroupItemsArrayIndex++] = item;
            }

            if (addFieldGroupItemsArray.length > 0) {
                (new AlertDialog.Builder(ProfileCreateActivity.this))
                        .setTitle(ERROR_DIALOG_TITLE)
                        .setItems(addFieldGroupItemsArray, (d, i) -> {
                            profileCreateParentAdapter.profileCreateParentDataList.add(
                                    new ProfileCreateParentData(
                                            profileCreateParentAdapter.profileCreateParentDataList.size(),
                                            ProfileCreateActivity.this,
                                            addFieldGroupItemsArray[i],
                                            null));
                            profileCreateParentAdapter.notifyItemInserted(profileCreateParentAdapter.profileCreateParentDataList.size() - 1);
                            recyclerView.scrollToPosition(profileCreateParentAdapter.getItemCount() - 1);
                        }).create().show();
            } else {
                Toast.makeText(ProfileCreateActivity.this, "더 이상 추가하실 수 있는 항목이 없습니다.", Toast.LENGTH_LONG).show();
            }
        });

        Toolbar toolbar = findViewById(R.id.ac_profileCreate_toolbar);
        toolbar.setNavigationOnClickListener((v) -> finish());
        toolbar.setOnMenuItemClickListener((item) -> {
            if (item.getItemId() == R.id.to_profileCreate_saveBtn) {
                String profileName = profileNameField.getText().toString().trim();
                String profileDescription = profileDescriptionField.getText().toString().trim();
                if (profileName.isEmpty()) {
                    AlertDialogGenerator.gen(
                            ProfileCreateActivity.this,
                            ERROR_DIALOG_TITLE,
                            "프로필의 이름을 입력해주세요.");
                    return false;
                }
                profileDescription = (profileDescription.isEmpty()) ? null : profileDescription;

                try {
                    ProfileAPI.create(ProfileCreateActivity.this, profileName, profileDescription, profileCreateParentAdapter.toJSONObject().toString())
                            .exceptionally((e) -> {
                                e.printStackTrace();
                                Throwable originalException = e.getCause();
                                AlertDialogGenerator.gen(
                                        ProfileCreateActivity.this,
                                        ERROR_DIALOG_TITLE,
                                        (originalException.getClass() == APIException.class)
                                                ? ((APIException) originalException).displayMsg
                                                : "프로필 정보를 보내서 처리하는 중 문제가 발생했습니다,\n잠시 후 다시 시도해주세요.");
                                return null;
                            });
                    Intent mainActivity = new Intent(ProfileCreateActivity.this, MainActivity.class);
                    mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainActivity);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    AlertDialogGenerator.gen(
                            ProfileCreateActivity.this,
                            ERROR_DIALOG_TITLE,
                            "프로필 정보를 보내는 중 문제가 발생했습니다,\n잠시 후 다시 시도해주세요.");
                }
            }
            return true;
        });
    }
}