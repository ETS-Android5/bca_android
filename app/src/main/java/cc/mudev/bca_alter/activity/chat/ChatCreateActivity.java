package cc.mudev.bca_alter.activity.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import cc.mudev.bca_alter.R;
import cc.mudev.bca_alter.activity.core.CoreLoginActivity;
import cc.mudev.bca_alter.activity.profile.ProfileDetailActivity;
import cc.mudev.bca_alter.adapter.ChatInvitedProfileAdapter;
import cc.mudev.bca_alter.adapter.ChatInvitedProfileData;
import cc.mudev.bca_alter.adapter.ProfileFollowingAdapter;
import cc.mudev.bca_alter.adapter.ProfileFollowingData;

public class ChatCreateActivity  extends AppCompatActivity {
    RecyclerView chatInvitedProfileRecyclerView, profileListRecyclerView;
    ChatInvitedProfileAdapter chatInvitedProfileAdapter;
    ProfileFollowingAdapter profileFollowingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_create);

        Toolbar toolbar = findViewById(R.id.ac_chatCreate_toolbar);
        toolbar.setNavigationOnClickListener(new Toolbar.OnClickListener() {
            @Override
            public void onClick(View view) { finish(); }
        });

        Button createRoomBtn = findViewById(R.id.ac_cardCreate_createRoomBtn);
        createRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Goto chat room page
                Intent chatRoomIntent = new Intent(view.getContext(), ChatRoomActivity.class);
                view.getContext().startActivity(chatRoomIntent);
                finish();
            }
        });

        ArrayList<ChatInvitedProfileData> chatInvitedProfileList = new ArrayList<>();

        chatInvitedProfileRecyclerView = findViewById(R.id.ac_chatCreate_selectedProfileRecyclerView);
        chatInvitedProfileRecyclerView.setLayoutManager(
                new LinearLayoutManager(
                        getApplicationContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false));

        chatInvitedProfileAdapter = new ChatInvitedProfileAdapter(chatInvitedProfileList);
        chatInvitedProfileAdapter.setOnItemClickListener(new ChatInvitedProfileAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                chatInvitedProfileList.remove(position);
                chatInvitedProfileAdapter.notifyItemRemoved(position);
            }
        }) ;
        chatInvitedProfileRecyclerView.setAdapter(chatInvitedProfileAdapter);

        ArrayList<ProfileFollowingData> profileFollowingList = new ArrayList<>();
        profileFollowingList.add(new ProfileFollowingData("김보라", "개발2팀"));
        profileFollowingList.add(new ProfileFollowingData("김환경", ""));
        profileFollowingList.add(new ProfileFollowingData("김희망", "영업팀"));
        profileFollowingList.add(new ProfileFollowingData("나다영", "R&D 연구팀"));
        profileFollowingList.add(new ProfileFollowingData("잔디", "환경 아티스트"));
        profileFollowingList.add(new ProfileFollowingData("박기수", "R&D 연구팀"));
        profileFollowingList.add(new ProfileFollowingData("임한울", ""));
        profileFollowingList.add(new ProfileFollowingData("이용", "성진개발 영업팀"));
        profileFollowingList.add(new ProfileFollowingData("안치홓", "서버 유지보수"));

        profileListRecyclerView = findViewById(R.id.ac_chatCreate_profileListRecyclerView);
        profileListRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        profileFollowingAdapter = new ProfileFollowingAdapter(profileFollowingList);
        profileFollowingAdapter.setOnItemClickListener(new ProfileFollowingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ProfileFollowingData targetProfileData = profileFollowingList.get(position);
                chatInvitedProfileList.add(new ChatInvitedProfileData(targetProfileData.name, ""));
                System.out.println(chatInvitedProfileList.size());
                chatInvitedProfileAdapter.notifyItemInserted(chatInvitedProfileList.size() - 1);
                scrollChatInvitedProfileRecyclerViewToEnd();
            }
        }) ;
        profileListRecyclerView.setAdapter(profileFollowingAdapter);
    }

    private void scrollChatInvitedProfileRecyclerViewToEnd() {
        chatInvitedProfileRecyclerView.scrollToPosition(chatInvitedProfileAdapter.getItemCount() - 1);
    }
}
