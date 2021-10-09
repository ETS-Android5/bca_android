package cc.mudev.bca_alter.activity.account;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import cc.mudev.bca_alter.R;
import cc.mudev.bca_alter.util.SwipeDismissBaseActivity;

public class AccountStatusActivity extends SwipeDismissBaseActivity {
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

