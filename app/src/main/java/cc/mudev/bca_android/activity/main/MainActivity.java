package cc.mudev.bca_android.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cc.mudev.bca_android.R;
import cc.mudev.bca_android.activity.card.CardShareQRcodeActivity;
import cc.mudev.bca_android.activity.core.CoreFrontActivity;
import cc.mudev.bca_android.activity.debug.DebugNetworkActivity;
import cc.mudev.bca_android.activity.profile.ProfileCreateActivity;
import cc.mudev.bca_android.activity.profile.ProfileDetailActivity;
import cc.mudev.bca_android.database.ChatDatabase;
import cc.mudev.bca_android.database.ProfileDatabase;
import cc.mudev.bca_android.database.TB_CHAT_ROOM;
import cc.mudev.bca_android.database.TB_PROFILE;
import cc.mudev.bca_android.network.APIRole;
import cc.mudev.bca_android.network.BCaAPI.AccountAPI;
import cc.mudev.bca_android.network.BCaAPI.ChatAPI;
import cc.mudev.bca_android.network.BCaAPI.ProfileDBSyncAPI;
import cc.mudev.bca_android.network.NetworkSupport;
import cc.mudev.bca_android.util.AlertDialogGenerator;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    private long backKeyPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton shareWithQRBtn = findViewById(R.id.ac_main_shareCardWithQRBtn);
        shareWithQRBtn.setOnClickListener((view) -> {
            Intent shareWithQRIntent = new Intent(MainActivity.this, CardShareQRcodeActivity.class);
            startActivity(shareWithQRIntent);
        });

        // TODO: Move this to chat list fragment
        try {
            ChatAPI.updateChatRoom(MainActivity.this)
                    .thenAcceptAsync((response) -> {
                        try {
                            JSONArray roomDataArray = response.body.data.getJSONArray("chat_rooms");
                            for (int i = 0; i < roomDataArray.length(); i++) {
                                JSONObject roomData = roomDataArray.getJSONObject(i);

                                ChatDatabase.getInstance(MainActivity.this).roomDao().insertRoom(
                                        new TB_CHAT_ROOM(
                                                roomData.getInt("uuid"),
                                                roomData.getString("name"),
                                                null,
                                                roomData.getInt("created_by_profile_id"),
                                                roomData.getString("commit_id"),
                                                roomData.getInt("created_at_int"),
                                                roomData.getInt("modified_at_int"),
                                                null,
                                                0,
                                                0
                                        )
                                );
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    })
                    .exceptionally((e) -> {
                        e.printStackTrace();
                        if (NetworkSupport.getInstance(MainActivity.this).isInternetOffline()) {
                            Toast.makeText(MainActivity.this, "인터넷이 연결되어 있지 않아 채팅방 정보를 업데이트할 수 없습니다", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "채팅방 정보를 업데이트하는 중 문제가 발생했습니다", Toast.LENGTH_LONG).show();
                        }
                        return null;
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

        DrawerLayout drawer = findViewById(R.id.main_drawerLayout);
        NavigationView navigationView = findViewById(R.id.ac_main_navView);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile_current,
                R.id.nav_account_signout,
                R.id.nav_debug_network)
                .setOpenableLayout(drawer)
                .build();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.ac_main_contentNavHostFragment);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener((item) -> {
            View currentView = drawer.getRootView();

            switch (item.getItemId()) {
                default:
                case R.id.nav_home:
                    // We're in home already, just ignore this.
                    break;

                case R.id.nav_profile_current: {
                    // Goto my profile info page
                    Intent myProfileIntent = new Intent(MainActivity.this, ProfileDetailActivity.class);
                    myProfileIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    NetworkSupport networkSupport = NetworkSupport.getInstance(MainActivity.this);
                    myProfileIntent.putExtra("profileId", networkSupport.getCurrentProfileId());
                    startActivity(myProfileIntent);
                    break;
                }
                case R.id.nav_account_signout: {
                    AlertDialogGenerator.gen(
                            MainActivity.this,
                            "B.Ca 로그아웃", "계정에서 로그아웃 하시겠습니까?\n모든 프로필에서 로그아웃하게 됩니다.",
                            false,
                            "로그아웃", (d, i) -> {
                                Toast.makeText(getApplicationContext(), "로그아웃 했습니다.\n다음에 또 만나요!", Toast.LENGTH_SHORT).show();
                                try {
                                    AccountAPI.signout(MainActivity.this);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Intent frontIntent = new Intent(currentView.getContext(), CoreFrontActivity.class);
                                frontIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                currentView.getContext().startActivity(frontIntent);
                                finishAffinity();
                            },
                            "취소", (d, i) -> {
                            }
                    );
                    break;
                }

                case R.id.nav_debug_network: {
                    // Goto network debug page
                    Intent networkDebugIntent = new Intent(MainActivity.this, DebugNetworkActivity.class);
                    networkDebugIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(networkDebugIntent);
                    break;
                }
            }

            //close navigation drawer
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });

        View navigationHeaderView = navigationView.getHeaderView(0);
        LinearLayout navViewHeaderLayout = navigationHeaderView.findViewById(R.id.na_header_main_linearLayout);
        navViewHeaderLayout.setOnClickListener((view) -> {
            PopupMenu profileSelectMenu = new PopupMenu(MainActivity.this, view);
            getMenuInflater().inflate(R.menu.menu_navheader, profileSelectMenu.getMenu());

            // Get profile id list
            ArrayList<Integer> profileIdList = new ArrayList<>();
            NetworkSupport networkSupport = NetworkSupport.getInstance(MainActivity.this);
            for (APIRole.APIRoleBase role : networkSupport.roleList.role) {
                if (role.getClass() == APIRole.ProfileRole.class) {
                    APIRole.ProfileRole targetProfile = (APIRole.ProfileRole) role;
                    profileIdList.add(targetProfile.id);
                }
            }

            ProfileDatabase profileDatabase = ProfileDatabase.getInstance(MainActivity.this);
            List<TB_PROFILE> myProfileList = profileDatabase.profileDao().loadByProfileID(profileIdList);
            for (TB_PROFILE myProfile : myProfileList) {
                profileSelectMenu.getMenu().add(0, myProfile.uuid, 0, myProfile.name);
            }

            profileSelectMenu.setOnMenuItemClickListener((menuItem) -> {
                switch (menuItem.getItemId()) {
                    case R.id.me_navheader_createNewProfileItem:
                        Intent createProfileIntent = new Intent(MainActivity.this, ProfileCreateActivity.class);
                        createProfileIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(createProfileIntent);
                        break;
                    default: {
                        int selectedProfileId = menuItem.getItemId();
                        if (selectedProfileId == networkSupport.currentProfile.id) {
                            Toast.makeText(MainActivity.this, "이미 해당 프로필입니다.", Toast.LENGTH_LONG).show();
                            break;
                        } else {
                            for (APIRole.APIRoleBase role : networkSupport.roleList.role)
                                if (role.getClass() == APIRole.ProfileRole.class && role.id == selectedProfileId) {
                                    networkSupport.currentProfile = (APIRole.ProfileRole) role;
                                    recreate();
                                    break;
                                }
                        }
                    }
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            });
            profileSelectMenu.show();
        });

        NetworkSupport networkSupport = NetworkSupport.getInstance(MainActivity.this);
        ProfileDatabase profileDatabase = ProfileDatabase.getInstance(MainActivity.this);
        TextView navViewHeaderProfileNameText = navigationHeaderView.findViewById(R.id.na_header_main_profileName);
        TextView navViewHeaderProfileDescriptionText = navigationHeaderView.findViewById(R.id.na_header_main_profileDescription);
        ImageView navViewHeaderProfileImageView = navigationHeaderView.findViewById(R.id.na_header_main_profileImage);

        int currentProfileId = networkSupport.getCurrentProfileId();
        if (currentProfileId > 0) {
            TB_PROFILE currentProfile = profileDatabase.profileDao().loadByProfileID(networkSupport.getCurrentProfileId());
            if (currentProfile == null) {
                // We need to try DB sync
                ProfileDBSyncAPI.updateDB(MainActivity.this).thenAcceptAsync((response) -> {
                    TB_PROFILE retCurrentProfile = profileDatabase.profileDao().loadByProfileID(networkSupport.getCurrentProfileId());

                    if (currentProfile == null) {
                        // Something went wrong
                        AlertDialogGenerator.gen(
                                MainActivity.this,
                                "어플리케이션을 시작할 수 없습니다", "프로필을 불러올 수 없습니다,\n다시 로그인 해 주세요.",
                                false, "확인", (d, i) -> {
                                    AccountAPI.signout(MainActivity.this);
                                    Intent frontIntent = new Intent(MainActivity.this, CoreFrontActivity.class);
                                    frontIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(frontIntent);
                                    finishAffinity();
                                });
                    }

                    navViewHeaderProfileNameText.setText(retCurrentProfile.name);
                    navViewHeaderProfileDescriptionText.setText((retCurrentProfile.description != null) ? retCurrentProfile.description : "");
                });
            } else {
                navViewHeaderProfileNameText.setText(currentProfile.name);
                navViewHeaderProfileDescriptionText.setText((currentProfile.description != null) ? currentProfile.description : "");

                if (!TextUtils.isEmpty(currentProfile.image_url)) {
                    NetworkSupport api = NetworkSupport.getInstance(MainActivity.this);

                    RequestOptions options = new RequestOptions()
                            .diskCacheStrategyOf(DiskCacheStrategy.ALL)
                            .centerCrop()
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_background);
                    LazyHeaders.Builder headerBuilder = new LazyHeaders.Builder();
                    try {
                        HashMap<String, String> headerData = api.getAuthenticatedHeader(false, true);
                        for (String key : headerData.keySet())
                            headerBuilder = headerBuilder.addHeader(key, headerData.get(key));
                    } catch (Exception e) {
                    }

                    Glide.with(MainActivity.this)
                            .load(new GlideUrl(api.baseUrl.substring(0, api.baseUrl.length() - 1) + currentProfile.image_url, headerBuilder.build()))
                            .thumbnail()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .apply(options)
                            .dontAnimate()
                            .dontTransform()
                            .into(navViewHeaderProfileImageView);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.profile_main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.ac_main_contentNavHostFragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        // 2500 milliseconds = 2.5 seconds
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG).show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            finish();
        }
    }
}
