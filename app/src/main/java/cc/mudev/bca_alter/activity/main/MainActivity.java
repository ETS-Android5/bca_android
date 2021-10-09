package cc.mudev.bca_alter.activity.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import cc.mudev.bca_alter.R;
import cc.mudev.bca_alter.activity.account.AccountSettingActivity;
import cc.mudev.bca_alter.activity.account.AccountStatusActivity;
import cc.mudev.bca_alter.activity.card.CardShareQRcodeActivity;
import cc.mudev.bca_alter.activity.core.CoreFrontActivity;
import cc.mudev.bca_alter.activity.core.CoreLoginActivity;
import cc.mudev.bca_alter.activity.core.CoreSettingActivity;
import cc.mudev.bca_alter.activity.debug.DebugNetworkActivity;
import cc.mudev.bca_alter.activity.profile.ProfileCreateActivity;
import cc.mudev.bca_alter.activity.profile.ProfileDetailActivity;
import cc.mudev.bca_alter.activity.profile.ProfileSettingActivity;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    private long backKeyPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton shareWithQRBtn = findViewById(R.id.ac_main_shareCardWithQRBtn);
        shareWithQRBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareWithQRIntent = new Intent(view.getContext(), CardShareQRcodeActivity.class);
                view.getContext().startActivity(shareWithQRIntent);
            }
        });

        DrawerLayout drawer = findViewById(R.id.main_drawerLayout);
        NavigationView navigationView = findViewById(R.id.ac_main_navView);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile_current,
                R.id.nav_account_status, R.id.nav_account_signout,
                R.id.nav_app_setting,
                R.id.nav_debug_network)
                .setOpenableLayout(drawer)
                .build();

        NavHostFragment navHostFragment = (NavHostFragment)getSupportFragmentManager().findFragmentById(R.id.ac_main_contentNavHostFragment);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                View currentView = drawer.getRootView();

                switch (item.getItemId()) {
                    default:
                    case R.id.nav_home:
                        // We're in home already, just ignore this.
                        break;

                    case R.id.nav_profile_current: {
                        // Goto my profile info page
                        Intent myProfileIntent = new Intent(currentView.getContext(), ProfileDetailActivity.class);
                        myProfileIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        currentView.getContext().startActivity(myProfileIntent);
                        break;
                    }
                    case R.id.nav_account_status: {
                        // Goto my account status page
                        Intent myAccountStatusIntent = new Intent(currentView.getContext(), AccountStatusActivity.class);
                        myAccountStatusIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        currentView.getContext().startActivity(myAccountStatusIntent);
                        break;
                    }
                    case R.id.nav_account_signout: {
                        AlertDialog signoutDialog = (new AlertDialog.Builder(currentView.getContext())
                            .setTitle("B.Ca 로그아웃")
                            .setMessage("계정에서 로그아웃 하시겠습니까?\n모든 프로필에서 로그아웃하게 됩니다.")
                            .setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    Toast.makeText(getApplicationContext(), "로그아웃 했습니다.\n다음에 또 만나요!", Toast.LENGTH_SHORT).show();
                                    // Goto Login page
                                    Intent frontIntent = new Intent(currentView.getContext(), CoreFrontActivity.class);
                                    frontIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    currentView.getContext().startActivity(frontIntent);
                                    finishAffinity();
                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            })).create();
                        signoutDialog.show();
                        break;
                    }
                    case R.id.nav_app_setting: {
                        // Goto app setting page
                        Intent appSettingIntent = new Intent(currentView.getContext(), CoreSettingActivity.class);
                        appSettingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        currentView.getContext().startActivity(appSettingIntent);
                        break;
                    }

                    case R.id.nav_debug_network: {
                        // Goto network debug page
                        Intent networkDebugIntent = new Intent(currentView.getContext(), DebugNetworkActivity.class);
                        networkDebugIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        currentView.getContext().startActivity(networkDebugIntent);
                        break;
                    }
                }

                //close navigation drawer
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        View navigationHeaderView = navigationView.getHeaderView(0);
        LinearLayout navViewHeaderLayout = navigationHeaderView.findViewById(R.id.na_header_main_linearLayout);
        navViewHeaderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu profileSelectMenu = new PopupMenu(getApplicationContext(), view);
                getMenuInflater().inflate(R.menu.menu_navheader, profileSelectMenu.getMenu());
                profileSelectMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.me_navheader_createNewProfileItem:
                                Intent createProfileIntent = new Intent(view.getContext(), ProfileCreateActivity.class);
                                createProfileIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                view.getContext().startActivity(createProfileIntent);
                                break;
                            default:
                                Toast.makeText(MainActivity.this, "메뉴 3 클릭", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        drawer.closeDrawer(GravityCompat.START);
                        return true;
                    }
                });
                profileSelectMenu.show();
            }
        });

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
            Toast toast = Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            finish();
        }
    }
}