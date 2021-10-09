package cc.mudev.bca_android.activity.account;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import cc.mudev.bca_android.R;
import cc.mudev.bca_android.util.SwipeDismissBaseActivity;

public class AccountSettingActivity extends SwipeDismissBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy_for_todo);

        Toolbar toolbar = findViewById(R.id.ac_dummyForTODO_toolbar);
        toolbar.setNavigationOnClickListener(new Toolbar.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}

