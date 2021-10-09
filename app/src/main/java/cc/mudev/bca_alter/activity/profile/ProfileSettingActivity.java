package cc.mudev.bca_alter.activity.profile;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import cc.mudev.bca_alter.R;
import cc.mudev.bca_alter.util.SwipeDismissBaseActivity;

public class ProfileSettingActivity extends SwipeDismissBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_recycler_only);

        Toolbar toolbar = findViewById(R.id.ac_commonRecyclerOnly_toolbar);
        toolbar.setTitle("차단된 사용자 목록");
        toolbar.setNavigationOnClickListener(new Toolbar.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}

