package cc.mudev.bca_android.activity.main.fragment_main.tab_chatlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import cc.mudev.bca_android.network.NetworkSupport;

public class ChatListFragment extends Fragment {
    private FragmentMainTabChatlistBinding binding;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainTabChatlistBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        FragmentActivity parentFragmentActivity = getParentFragment().getActivity();
        Context appContext = root.getContext();

        Toolbar toolbar = root.findViewById(R.id.fr_chatList_toolbar);
        toolbar.setNavigationOnClickListener((view) -> ((DrawerLayout) parentFragmentActivity.findViewById(R.id.main_drawerLayout)).openDrawer(GravityCompat.START));
        toolbar.setOnMenuItemClickListener((item) -> {
            switch (item.getItemId()) {
                case R.id.to_chatList_createNewRoomMenu: {
                    // Goto chat room create activity
                    Intent chatCreateIntent = new Intent(appContext, ChatCreateActivity.class);
                    appContext.startActivity(chatCreateIntent);
                    break;
                }
                default:
                    break;
            }
            return true;
        });

        ArrayList<ChatRoomListData> chatRoomList = new ArrayList<>();
        if (NetworkSupport.getInstance(appContext).getCurrentProfileId() == 1) {

            chatRoomList.add(new ChatRoomListData("김정환", "그건 모르지 ㅋㅋㅋㅋ", 0));
            chatRoomList.add(new ChatRoomListData("신정은 선생님", "없을경우 등록 확인해보시길 바랍니다.", 0));
            chatRoomList.add(new ChatRoomListData("임수만 부장님", "알겠습니다.", 0));
            chatRoomList.add(new ChatRoomListData("임수만 부장님", "알겠습니다.", 0));
            chatRoomList.add(new ChatRoomListData("뼈돌이", "ㅇㅋㅇㅋ", 0));
            chatRoomList.add(new ChatRoomListData("박주아", "없는 줄 알았는데 가방 뒤져보니 나오네요ㅠㅜ", 1));
            chatRoomList.add(new ChatRoomListData("최성훈", "도감 포기한지 오랩니다 ㅋㅋㅋ", 310));
            chatRoomList.add(new ChatRoomListData("장서희", "보냈습니다. 즐거운 하루 되시길 바랍니다.", 1));
            chatRoomList.add(new ChatRoomListData("김지성", "ㅇㅇ 내일 근육통 심할듯ㅋㅋㅋㅋ", 290));
            Collections.sort(chatRoomList);
        } else {
            ChatDatabase chatDB = ChatDatabase.getInstance(appContext);
            int currentProfileId = NetworkSupport.getInstance(appContext).getCurrentProfileId();
            List<TB_CHAT_ROOM> chatRooms = chatDB.roomDao().getAllChatRoom(currentProfileId);
            for (TB_CHAT_ROOM chatRoom : chatRooms) {
                TB_CHAT_EVENT latestMessage = chatDB.eventDao().getLatestMessage(chatRoom.uuid);
                chatRoomList.add(new ChatRoomListData(chatRoom.name, latestMessage.message, 0));
            }
        }

        RecyclerView recyclerView = root.findViewById(R.id.fr_chatList_chatListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(appContext));
        ChatRoomListAdapter adapter = new ChatRoomListAdapter(chatRoomList);
        adapter.setOnItemClickListener((view, position) -> {
            Intent chatRoomIntent = new Intent(appContext, ChatRoomActivity.class);
            appContext.startActivity(chatRoomIntent);
        });
        recyclerView.setAdapter(adapter);

        if (chatRoomList.isEmpty()) {
            recyclerView.setVisibility(View.INVISIBLE);
            ((TextView)root.findViewById(R.id.fr_chatList_noChatListMsg)).setVisibility(View.VISIBLE);
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
