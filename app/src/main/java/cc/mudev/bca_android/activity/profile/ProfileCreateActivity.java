package cc.mudev.bca_android.activity.profile;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import cc.mudev.bca_android.adapter.ProfileCreateParentAdapter;
import cc.mudev.bca_android.adapter.ProfileCreateParentData;
import cc.mudev.bca_android.adapter.ProfileCreateParentItemTouchHelperCallback;

public class ProfileCreateActivity extends AppCompatActivity {
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

        Button addFieldGroupBtn = findViewById(R.id.ac_profileCreate_itemAddBtn);
        addFieldGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // We need to filter already added group name
                String[] addFieldGroupDefaultItems = getResources().getStringArray(R.array.sa_profileCreate_addGroupDialogItem);
                ArrayList<String> addFieldGroupItems = new ArrayList(Arrays.asList(addFieldGroupDefaultItems));
                for (ProfileCreateParentData prpData: profileCreateParentAdapter.profileCreateParentDataList) {
                    addFieldGroupItems.remove(prpData.childDataName);
                }
                String[] addFieldGroupItemsArray = new String[addFieldGroupItems.size()];
                int addFieldGroupItemsArrayIndex = 0;
                for (String item: addFieldGroupItems) { addFieldGroupItemsArray[addFieldGroupItemsArrayIndex++] = item; }

                if (addFieldGroupItemsArray.length > 0) {
                    (new AlertDialog.Builder(v.getContext()))
                        .setTitle("추가하실 항목을 선택해주세요.")
                        .setItems(addFieldGroupItemsArray, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position) {
                                profileCreateParentAdapter.profileCreateParentDataList.add(
                                    new ProfileCreateParentData(
                                        profileCreateParentAdapter.profileCreateParentDataList.size(),
                                        v.getContext(),
                                        addFieldGroupItemsArray[position],
                                        null));
                                profileCreateParentAdapter.notifyItemInserted(profileCreateParentAdapter.profileCreateParentDataList.size() - 1);
                                recyclerView.scrollToPosition(profileCreateParentAdapter.getItemCount() - 1);
                            }
                        }).create().show();
                } else {
                    Toast.makeText(getApplicationContext(), "더 이상 추가하실 수 있는 항목이 없습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });

        Toolbar toolbar = findViewById(R.id.ac_profileCreate_toolbar);
        toolbar.setNavigationOnClickListener(new Toolbar.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.to_profileCreate_saveBtn:
                        System.out.println(profileCreateParentAdapter.toJSONObject());
                        break;
                    default:
                        break;
                }
                // finish();
                return true;
            }
        });
    }
}