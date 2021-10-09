package cc.mudev.bca_alter.activity.main.fragment_main.tab_chatlist;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

import cc.mudev.bca_alter.R;
import cc.mudev.bca_alter.activity.chat.ChatCreateActivity;
import cc.mudev.bca_alter.activity.chat.ChatRoomActivity;
import cc.mudev.bca_alter.activity.core.CoreLoginActivity;
import cc.mudev.bca_alter.activity.profile.ProfileDetailActivity;
import cc.mudev.bca_alter.adapter.ChatRoomListAdapter;
import cc.mudev.bca_alter.adapter.ChatRoomListData;
import cc.mudev.bca_alter.adapter.ProfileFollowingAdapter;
import cc.mudev.bca_alter.databinding.FragmentMainTabChatlistBinding;

public class ChatListFragment extends Fragment {
    private FragmentMainTabChatlistBinding binding;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainTabChatlistBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Toolbar toolbar = root.findViewById(R.id.fr_chatList_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = getParentFragment().getActivity().findViewById(R.id.main_drawerLayout);
                drawer.openDrawer(GravityCompat.START);
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int order = item.getItemId();
                switch (order) {
                    case R.id.to_chatList_createNewRoomMenu: {
                        // Goto chat room create activity
                        Intent chatCreateIntent = new Intent(root.getContext(), ChatCreateActivity.class);
                        root.getContext().startActivity(chatCreateIntent);
                        break;
                    }
                    default:
                        break;
                }
                return true;
            }
        });

        ArrayList<ChatRoomListData> chatRoomList = new ArrayList<>();
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

        RecyclerView recyclerView = root.findViewById(R.id.fr_chatList_chatListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));

        ChatRoomListAdapter adapter = new ChatRoomListAdapter(chatRoomList);
        adapter.setOnItemClickListener(new ChatRoomListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent chatRoomIntent = new Intent(view.getContext(), ChatRoomActivity.class);
                view.getContext().startActivity(chatRoomIntent);
            }
        }) ;
        recyclerView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
