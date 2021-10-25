package cc.mudev.bca_android.activity.chat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.CompletionException;

import cc.mudev.bca_android.R;
import cc.mudev.bca_android.adapter.ChatInvitedProfileAdapter;
import cc.mudev.bca_android.adapter.ChatInvitedProfileData;
import cc.mudev.bca_android.adapter.ProfileFollowingAdapter;
import cc.mudev.bca_android.adapter.ProfileFollowingData;
import cc.mudev.bca_android.database.ChatDatabase;
import cc.mudev.bca_android.database.TB_CHAT_ROOM;
import cc.mudev.bca_android.network.BCaAPI.ChatAPI;

public class ChatCreateActivity extends AppCompatActivity {
    private static final String ERROR_DIALOG_TITLE = "채팅방을 만들 수 없어요";
    RecyclerView chatInvitedProfileRecyclerView, profileListRecyclerView;
    ChatInvitedProfileAdapter chatInvitedProfileAdapter;
    ProfileFollowingAdapter profileFollowingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_create);

        Toolbar toolbar = findViewById(R.id.ac_chatCreate_toolbar);
        toolbar.setNavigationOnClickListener((view) -> finish());

        chatInvitedProfileRecyclerView = findViewById(R.id.ac_chatCreate_selectedProfileRecyclerView);
        chatInvitedProfileRecyclerView.setLayoutManager(
                new LinearLayoutManager(
                        ChatCreateActivity.this,
                        LinearLayoutManager.HORIZONTAL,
                        false));

        ArrayList<ChatInvitedProfileData> chatInvitedProfileList = new ArrayList<>();
        chatInvitedProfileAdapter = new ChatInvitedProfileAdapter(chatInvitedProfileList);
        chatInvitedProfileAdapter.setOnItemClickListener((view, position) -> {
            chatInvitedProfileList.remove(position);
            chatInvitedProfileAdapter.notifyItemRemoved(position);
        });
        chatInvitedProfileRecyclerView.setAdapter(chatInvitedProfileAdapter);

        profileListRecyclerView = findViewById(R.id.ac_chatCreate_profileListRecyclerView);
        profileListRecyclerView.setLayoutManager(new LinearLayoutManager(ChatCreateActivity.this));

        profileFollowingAdapter = new ProfileFollowingAdapter();
        profileListRecyclerView.setAdapter(profileFollowingAdapter);
        profileFollowingAdapter.setOnItemClickListener((view, position) -> {
            ProfileFollowingData targetProfileData = profileFollowingAdapter.infoListData.get(position);

            for (ChatInvitedProfileData invitedProfileData : chatInvitedProfileList)
                if (invitedProfileData.profileId == targetProfileData.profileId)
                    return;

            chatInvitedProfileList.add(new ChatInvitedProfileData(targetProfileData.name, targetProfileData.imageUrl, targetProfileData.profileId));
            chatInvitedProfileAdapter.notifyItemInserted(chatInvitedProfileList.size() - 1);
            chatInvitedProfileRecyclerView.scrollToPosition(chatInvitedProfileAdapter.getItemCount() - 1);
        });

        Button createRoomBtn = findViewById(R.id.ac_chatCreate_createRoomBtn);
        createRoomBtn.setOnClickListener((view) -> {
            // Goto chat room page
            ArrayList<Integer> invitedProfileIdList = new ArrayList<>();
            for (ChatInvitedProfileData profileData : chatInvitedProfileList)
                invitedProfileIdList.add(profileData.profileId);

            // TODO: Rewrite below
            String profileListString = "";
            try {
                profileListString = (new JSONArray(invitedProfileIdList)).toString();
                ChatAPI.createChatRoom(ChatCreateActivity.this, profileListString)
                        .thenComposeAsync((response1) -> {
                            try {
                                return ChatAPI.updateChatRoom(ChatCreateActivity.this)
                                        .thenAcceptAsync((response) -> {
                                            try {
                                                JSONArray roomDataArray = response.body.data.getJSONArray("chat_rooms");
                                                for (int i = 0; i < roomDataArray.length(); i++) {
                                                    JSONObject roomData = roomDataArray.getJSONObject(i);

                                                    ChatDatabase.getInstance(ChatCreateActivity.this).roomDao().insertRoom(
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
                                                    Intent chatRoomIntent = new Intent(ChatCreateActivity.this, ChatRoomActivity.class);
                                                    chatRoomIntent.putExtra("roomId", response1.body.data.getJSONObject("chat_room").getInt("uuid"));
                                                    startActivity(chatRoomIntent);
                                                    finish();
                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        })
                                        .exceptionally((e) -> {
                                            e.printStackTrace();
                                            return null;
                                        });
                            } catch (Exception e) {
                                throw new CompletionException(e);
                            }
                        })
                        .exceptionally((e) -> {
                            e.printStackTrace();
                            return null;
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
