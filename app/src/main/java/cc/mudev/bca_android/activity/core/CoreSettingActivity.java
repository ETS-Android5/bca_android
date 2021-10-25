package cc.mudev.bca_android.activity.core;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import cc.mudev.bca_android.R;

public class CoreSettingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy_for_todo);

        Toolbar toolbar = findViewById(R.id.ac_dummyForTODO_toolbar);
        toolbar.setNavigationOnClickListener((view) -> finish());
    }
}

