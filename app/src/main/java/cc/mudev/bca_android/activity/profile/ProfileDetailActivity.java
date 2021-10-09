package cc.mudev.bca_android.activity.profile;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import java.util.ArrayList;

import cc.mudev.bca_android.R;
import cc.mudev.bca_android.adapter.ProfileDetailCardListAdapter;
import cc.mudev.bca_android.adapter.ProfileDetailCardListData;
import cc.mudev.bca_android.adapter.ProfileDetailInfoAdapter;
import cc.mudev.bca_android.adapter.ProfileDetailInfoData;

public class ProfileDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);

        TextView profileTeamText = findViewById(R.id.ac_profileDetail_teamNameText);
        TextView profileDescription = findViewById(R.id.ac_profileDetail_descriptionText);

        Toolbar toolbar = findViewById(R.id.ac_profileDetail_toolbar);
        toolbar.setTitle("홍길동");
        toolbar.setNavigationOnClickListener(new Toolbar.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                // I don't know why, I don't want to know why, I shouldn't
                // have to wonder why, but for whatever reason this stupid
                // recyclerview do reordering when we try to supportFinishAfterTransition
                //  supportFinishAfterTransition();
            }
        });

        ArrayList<ProfileDetailCardListData> cardDataList = new ArrayList<>();
        cardDataList.add(new ProfileDetailCardListData(0, "card_2", ""));
        cardDataList.add(new ProfileDetailCardListData(0, "card_1", ""));
        cardDataList.add(new ProfileDetailCardListData(0, "card_3", ""));
        cardDataList.add(new ProfileDetailCardListData(0, "card_4", ""));
        cardDataList.add(new ProfileDetailCardListData(0, "card_5", ""));

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int cardWidthDpAsPixels = (int)((316 + 8) * getResources().getDisplayMetrics().density + 0.5f);

        RecyclerView profileCardRecyclerView = findViewById(R.id.ac_profileDetail_profileCardRecyclerView);
        int paddingStartEnd = (width - cardWidthDpAsPixels) / 2;
        profileCardRecyclerView.setPadding(paddingStartEnd, 0, paddingStartEnd, 0);

        profileCardRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(profileCardRecyclerView);

        ProfileDetailCardListAdapter profileDetailCardListAdapter = new ProfileDetailCardListAdapter(cardDataList);
        profileCardRecyclerView.setAdapter(profileDetailCardListAdapter);

        ArrayList<ProfileDetailInfoData> infoDataList = new ArrayList<>();
        infoDataList.add(new ProfileDetailInfoData("전화번호(회사)", "010-7342-7123"));
        infoDataList.add(new ProfileDetailInfoData("전화번호(폰)", "010-9911-1992"));
        infoDataList.add(new ProfileDetailInfoData("이메일(네이버)", "rlabusic92@naver.com"));
        infoDataList.add(new ProfileDetailInfoData("이메일(구글)", "rlabusic92@gmail.com"));
        infoDataList.add(new ProfileDetailInfoData("주소", "경기도 성남시 분당구 정자동 불정로 6 그린팩토리"));
        infoDataList.add(new ProfileDetailInfoData("전화번호(회사)", "010-7342-7123"));
        infoDataList.add(new ProfileDetailInfoData("전화번호(폰)", "010-9911-1992"));
        infoDataList.add(new ProfileDetailInfoData("이메일(네이버)", "rlabusic92@naver.com"));
        infoDataList.add(new ProfileDetailInfoData("이메일(구글)", "rlabusic92@gmail.com"));
        infoDataList.add(new ProfileDetailInfoData("주소", "경기도 성남시 분당구 정자동 불정로 6 그린팩토리"));

        RecyclerView recyclerView = findViewById(R.id.ac_profileDetail_profileInfoRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ProfileDetailInfoAdapter adapter = new ProfileDetailInfoAdapter(infoDataList);
        recyclerView.setAdapter(adapter);
    }
}