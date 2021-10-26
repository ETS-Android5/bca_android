package cc.mudev.bca_android.activity.profile;

import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CompletionException;

import cc.mudev.bca_android.R;
import cc.mudev.bca_android.adapter.ProfileDetailCardListAdapter;
import cc.mudev.bca_android.adapter.ProfileDetailCardListData;
import cc.mudev.bca_android.adapter.ProfileDetailInfoAdapter;
import cc.mudev.bca_android.adapter.ProfileDetailInfoData;
import cc.mudev.bca_android.database.ProfileDatabase;
import cc.mudev.bca_android.database.TB_PROFILE;
import cc.mudev.bca_android.network.APIException;
import cc.mudev.bca_android.network.BCaAPI.ProfileAPI;
import cc.mudev.bca_android.network.NetworkSupport;
import cc.mudev.bca_android.util.AlertDialogGenerator;
import cc.mudev.bca_android.util.image.ImageRotation;

public class ProfileDetailActivity extends AppCompatActivity {
    private static final String ERROR_DIALOG_TITLE = "프로필 정보를 가져올 수 없어요";
    Toolbar toolbar;
    TextView profileDescriptionText;
    CircularImageView profileImageView;
    RecyclerView cardRecyclerView;
    RecyclerView infoRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);

        this.toolbar = findViewById(R.id.ac_profileDetail_toolbar);
        this.toolbar.setNavigationOnClickListener((v) -> {
            // I don't know why, I don't want to know why, I shouldn't
            // have to wonder why, but for whatever reason this stupid
            // recyclerview do reordering when we try to supportFinishAfterTransition
            //  supportFinishAfterTransition();
            finish();
        });
        this.profileImageView = findViewById(R.id.ac_profileDetail_profileImageView);
        this.profileDescriptionText = findViewById(R.id.ac_profileDetail_teamNameText);
        this.infoRecyclerView = findViewById(R.id.ac_profileDetail_profileInfoRecyclerView);
        this.cardRecyclerView = findViewById(R.id.ac_profileDetail_profileCardRecyclerView);

        int profileId = getIntent().getIntExtra("profileId", -1);
        if (profileId < 0) {
            AlertDialogGenerator.gen(ProfileDetailActivity.this,
                    ERROR_DIALOG_TITLE, "프로필 고유 번호를 가져오는 도중 문제가 발생했습니다.",
                    false,
                    "확인", (d, i) -> finish());
            return;
        }

        NetworkSupport api = NetworkSupport.getInstance(ProfileDetailActivity.this);
        for (Integer myProfileId : api.getAllProfileIds()) {
            if (profileId == myProfileId) {
                // This is user's profile. Add modify menu on toolbar.
                this.toolbar.inflateMenu(R.menu.toolbar_profilemodify);
                break;
            }
        }

        if (!this.loadProfileDataFromLocalDB(profileId)) {
            // Maybe there's no DB record about this profile. Try to load profile from Server
            this.loadProfileDataFromNetwork(profileId);
        }
    }

    public boolean loadProfileDataFromLocalDB(int profileId) {
        ProfileDatabase profileDatabase = ProfileDatabase.getInstance(ProfileDetailActivity.this);
        TB_PROFILE profileData = profileDatabase.profileDao().loadByProfileID(profileId);
        if (profileData == null)
            return false;

        try {
            setupToolbarTitle(profileData.name, profileData.team_name, profileData.image_url);
            setupCardRecyclerView(new ArrayList<>());
            setupProfileInfoRecyclerView(jsonStringToProfileDetailInfoData(profileData.data));
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public void loadProfileDataFromNetwork(int profileId) {
        NetworkSupport api = NetworkSupport.getInstance(ProfileDetailActivity.this);
        try {
            ProfileAPI.load(ProfileDetailActivity.this, profileId)
                    .thenAcceptAsync((response) -> {
                        // Great! We successfully pulled profile data from the server!
                        try {
                            // TODO: Load card data list from network
                            JSONObject profileRow = response.body.data.getJSONObject("profile");
                            String profileName = profileRow.getString("name");
                            String profileTeamName = profileRow.getString("team_name");
                            String profileImageURL = profileRow.getString("image_url");
                            String profileData = profileRow.getString("data");

                            setupToolbarTitle(profileName, profileTeamName, profileImageURL);
                            setupCardRecyclerView(new ArrayList<>());
                            setupProfileInfoRecyclerView(jsonStringToProfileDetailInfoData(profileData));
                        } catch (Exception e) {
                            throw new CompletionException(e);
                        }
                    })
                    .exceptionally((e) -> {
                        e.printStackTrace();
                        Throwable originalException = e.getCause();
                        AlertDialogGenerator.gen(
                                ProfileDetailActivity.this,
                                ERROR_DIALOG_TITLE,
                                api.isOffline
                                        ? "오프라인 상태에서 프로필을 가져올 수 없습니다."
                                        : ((originalException.getClass() == APIException.class)
                                        ? ((APIException) originalException).displayMsg
                                        : "프로필 정보를 가져오는 도중 문제가 발생했습니다."),
                                false,
                                "확인", (d, i) -> finish());
                        return null;
                    });
        } catch (Exception e) {
            AlertDialogGenerator.gen(ProfileDetailActivity.this,
                    ERROR_DIALOG_TITLE,
                    api.isOffline
                            ? "오프라인 상태에서 프로필을 가져올 수 없습니다."
                            : "프로필 정보를 가져오는 도중 문제가 발생했습니다.",
                    false, "확인", (d, i) -> finish());
        }
    }

    public ArrayList<ProfileDetailInfoData> jsonStringToProfileDetailInfoData(String jsonStr) {
        Comparator<JSONObject> indexOrderComp = (j1, j2) -> {
            try {
                return Integer.valueOf(j1.getInt("index")).compareTo(j2.getInt("index"));
            } catch (JSONException e) {
                return 0;
            }
        };

        // Setup profile information data
        ArrayList<ProfileDetailInfoData> result = new ArrayList<>();
        try {
            ArrayList<JSONObject> tempJSONObjectData = new ArrayList<>();
            JSONObject dataJson = new JSONObject(jsonStr);
            for (Iterator<String> it = dataJson.keys(); it.hasNext(); ) {
                String key = it.next();
                tempJSONObjectData.add(dataJson.getJSONObject(key).put("key", key));
            }
            tempJSONObjectData.sort(indexOrderComp);

            for (JSONObject j : tempJSONObjectData) {
                ArrayList<JSONObject> tempChildJSONObjectData = new ArrayList<>();
                JSONObject d = j.getJSONObject("value");
                for (Iterator<String> it = d.keys(); it.hasNext(); ) {
                    String key = it.next();
                    tempChildJSONObjectData.add(d.getJSONObject(key).put("key", key));
                }
                tempChildJSONObjectData.sort(indexOrderComp);

                for (JSONObject actualData : tempChildJSONObjectData) {
                    String bigTitle = profileInfoLabelTranslator(j.getString("key"));
                    String subTitle = actualData.getString("key");
                    result.add(new ProfileDetailInfoData(bigTitle + " - " + subTitle, actualData.getString("value")));
                }
            }
        } catch (Exception e) {
            return null;
        }
        return result;
    }

    public void setupToolbarTitle(String title, String teamName, String imageUrl) {
        this.toolbar.setTitle(title);

        // Setup profile's cards
        if (!TextUtils.isEmpty(teamName))
            this.profileDescriptionText.setText(teamName);

        if (!TextUtils.isEmpty(imageUrl)) {
            NetworkSupport api = NetworkSupport.getInstance(ProfileDetailActivity.this);

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .diskCacheStrategyOf(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background);
            LazyHeaders.Builder headerBuilder = new LazyHeaders.Builder();
            try {
                HashMap<String, String> headerData = api.getAuthenticatedHeader(false, true);
                for (String key : headerData.keySet())
                    headerBuilder = headerBuilder.addHeader(key, headerData.get(key));
            } catch (Exception e) {
            }

            Glide.with(ProfileDetailActivity.this)
                    .load(new GlideUrl(api.baseUrl.substring(0, api.baseUrl.length() - 1) + imageUrl, headerBuilder.build()))
                    .thumbnail()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .apply(options)
                    .dontAnimate()
                    .dontTransform()
                    .into(this.profileImageView);
        } else {
            this.profileImageView.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    public void setupCardRecyclerView(ArrayList<ProfileDetailCardListData> cardDataList) {

        // Setup card data
        // TODO: Get real card data from DB
        cardDataList.add(new ProfileDetailCardListData(0, "card_2", "", false));
        cardDataList.add(new ProfileDetailCardListData(0, "card_1", "", false));
        cardDataList.add(new ProfileDetailCardListData(0, "card_3", "", false));
        cardDataList.add(new ProfileDetailCardListData(0, "card_4", "", false));
        cardDataList.add(new ProfileDetailCardListData(0, "card_5", "", false));

        // Setup margin of first and last item
        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        int cardWidthDpAsPixels = (int) ((316 + 8) * getResources().getDisplayMetrics().density + 0.5f);
        int paddingStartEnd = (screenSize.x - cardWidthDpAsPixels) / 2;
        this.cardRecyclerView.setPadding(paddingStartEnd, 0, paddingStartEnd, 0);

        // Setup layoutManager and adapter. There's nothing special here.
        this.cardRecyclerView.setLayoutManager(new LinearLayoutManager(ProfileDetailActivity.this, LinearLayoutManager.HORIZONTAL, false));
        (new LinearSnapHelper()).attachToRecyclerView(this.cardRecyclerView);
        ProfileDetailCardListAdapter profileDetailCardListAdapter = new ProfileDetailCardListAdapter(cardDataList);
        this.cardRecyclerView.setAdapter(profileDetailCardListAdapter);
    }

    public void setupProfileInfoRecyclerView(ArrayList<ProfileDetailInfoData> infoDataList) {
        this.infoRecyclerView.setLayoutManager(new LinearLayoutManager(ProfileDetailActivity.this));
        ProfileDetailInfoAdapter adapter = new ProfileDetailInfoAdapter(infoDataList);
        this.infoRecyclerView.setAdapter(adapter);
    }

    public static String profileInfoLabelTranslator(String label) {
        if (TextUtils.isEmpty(label))
            label = "";

        switch (label) {
            case "phone":
                return "전화번호";
            case "address":
                return "주소";
            case "email":
                return "이메일 주소";
            default:
                return label;
        }
    }
}