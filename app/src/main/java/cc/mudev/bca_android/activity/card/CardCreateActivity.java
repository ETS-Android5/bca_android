package cc.mudev.bca_android.activity.card;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import cc.mudev.bca_android.R;

public class CardCreateActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_create);

        Toolbar toolbar = findViewById(R.id.ac_cardCreate_toolbar);
        toolbar.setNavigationOnClickListener(new Toolbar.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int order = item.getItemId();
                switch (order) {
                    case R.id.to_cardCreate_saveBtn: {
                        finish();
                        break;
                    }
                    default:
                        break;
                }
                return true;
            }
        });

    }
}

