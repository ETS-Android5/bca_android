package cc.mudev.bca_android.activity.main.fragment_main.tab_chatlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cc.mudev.bca_android.R;
import cc.mudev.bca_android.activity.chat.ChatCreateActivity;
import cc.mudev.bca_android.activity.chat.ChatRoomActivity;
import cc.mudev.bca_android.adapter.ChatRoomListAdapter;
import cc.mudev.bca_android.adapter.ChatRoomListData;
import cc.mudev.bca_android.database.ChatDatabase;
import cc.mudev.bca_android.database.TB_CHAT_EVENT;
import cc.mudev.bca_android.database.TB_CHAT_ROOM;
import cc.mudev.bca_android.databinding.FragmentMainTabChatlistBinding;
import cc.mudev.bca_android.network.BCaAPI.ChatAPI;
import cc.mudev.bca_android.network.NetworkSupport;

public class ChatListFragment extends Fragment {
    private static final String TAG = "ChatListFragment";
    private FragmentMainTabChatlistBinding binding;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainTabChatlistBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        FragmentActivity parentFragmentActivity = getParentFragment().getActivity();
        Context appContext = root.getContext();

        Toolbar toolbar = root.findViewById(R.id.fr_chatList_toolbar);
        toolbar.setNavigationOnClickListener((view) -> ((DrawerLayout) parentFragmentActivity.findViewById(R.id.main_drawerLayout)).openDrawer(GravityCompat.START));
        toolbar.setOnMenuItemClickListener((item) -> {
            if (item.getItemId() == R.id.to_chatList_createNewRoomMenu) {// Goto chat room create activity
                Intent chatCreateIntent = new Intent(appContext, ChatCreateActivity.class);
                appContext.startActivity(chatCreateIntent);
            }
            return true;
        });

        ArrayList<ChatRoomListData> chatRoomList = new ArrayList<>();
        RecyclerView recyclerView = root.findViewById(R.id.fr_chatList_chatListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(appContext));
        ChatRoomListAdapter adapter = new ChatRoomListAdapter(chatRoomList);
        adapter.setOnItemClickListener((view, position) -> {
            Intent chatRoomIntent = new Intent(appContext, ChatRoomActivity.class);
            chatRoomIntent.putExtra("chatId", chatRoomList.get(position).uuid);
            appContext.startActivity(chatRoomIntent);
        });
        recyclerView.setAdapter(adapter);

        try {
            ChatAPI.updateChatRoom(appContext)
                    .thenAcceptAsync((response) -> {
                        ChatDatabase chatDB = ChatDatabase.getInstance(appContext);
                        int currentProfileId = NetworkSupport.getInstance(appContext).getCurrentProfileId();
                        List<TB_CHAT_ROOM> chatRooms = chatDB.roomDao().getAllChatRoom(currentProfileId);
                        for (TB_CHAT_ROOM chatRoom : chatRooms) {
                            TB_CHAT_EVENT latestMessage = chatDB.eventDao().getLatestMessage(chatRoom.uuid);
                            String latestMessageStr = (latestMessage != null) ? latestMessage.message : "";
                            chatRoomList.add(new ChatRoomListData(chatRoom.uuid, chatRoom.name, latestMessageStr, 0));
                        }
                    })
                    .exceptionally((e) -> {
                        e.printStackTrace();
                        return null;
                    })
                    .whenCompleteAsync((result, ex) ->
                            (new Handler(Looper.getMainLooper())).postDelayed(() -> {
                                adapter.notifyDataSetChanged();

                                if (chatRoomList.isEmpty()) {
                                    recyclerView.setVisibility(View.INVISIBLE);
                                    root.findViewById(R.id.fr_chatList_noChatListMsg).setVisibility(View.VISIBLE);
                                }
                            }, 0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
