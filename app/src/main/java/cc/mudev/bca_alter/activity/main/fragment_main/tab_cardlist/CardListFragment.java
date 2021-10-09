package cc.mudev.bca_alter.activity.main.fragment_main.tab_cardlist;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import java.util.ArrayList;

import cc.mudev.bca_alter.R;
import cc.mudev.bca_alter.activity.card.CardCreateActivity;
import cc.mudev.bca_alter.activity.card.CardShareQRcodeActivity;
import cc.mudev.bca_alter.activity.chat.ChatCreateActivity;
import cc.mudev.bca_alter.adapter.CardListOnMainFragmentAdapter;
import cc.mudev.bca_alter.adapter.ProfileDetailCardListAdapter;
import cc.mudev.bca_alter.adapter.ProfileDetailCardListData;
import cc.mudev.bca_alter.databinding.FragmentMainTabCardlistBinding;

public class CardListFragment extends Fragment {
    private FragmentMainTabCardlistBinding binding;
    public int currentCardIndex = 0;
    public boolean isCardRecyclerViewIsMoving = false;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainTabCardlistBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Toolbar toolbar = root.findViewById(R.id.fr_cardlist_toolbar);
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
                    case R.id.to_cardList_createCardBtn: {
                        // Goto chat room create activity
                        Intent cardCreateIntent = new Intent(root.getContext(), CardCreateActivity.class);
                        root.getContext().startActivity(cardCreateIntent);
                        break;
                    }
                    default:
                        break;
                }
                return true;
            }
        });

        ArrayList<ProfileDetailCardListData> cardDataList = new ArrayList<>();
        cardDataList.add(new ProfileDetailCardListData(0, "card_1", ""));
        cardDataList.add(new ProfileDetailCardListData(0, "card_2", ""));
        cardDataList.add(new ProfileDetailCardListData(0, "card_3", ""));
        cardDataList.add(new ProfileDetailCardListData(0, "card_4", ""));
        cardDataList.add(new ProfileDetailCardListData(0, "card_5", ""));

        Display display = getParentFragment().getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int cardWidthDpAsPixels = (int) ((225 + 16) * getResources().getDisplayMetrics().density + 0.5f);

        RecyclerView cardListRecyclerView = root.findViewById(R.id.fr_cardlist_cardRecyclerView);
        int paddingStartEnd = (width - cardWidthDpAsPixels) / 2;
        cardListRecyclerView.setPadding(paddingStartEnd, 0, paddingStartEnd, 0);

        RecyclerView.LayoutManager cardLinearLayoutManager = new LinearLayoutManager(getParentFragment().getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        cardListRecyclerView.setLayoutManager(cardLinearLayoutManager);
        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(cardListRecyclerView);

        CardListOnMainFragmentAdapter cardListOnMainFragmentAdapter = new CardListOnMainFragmentAdapter(cardDataList);
        cardListRecyclerView.setAdapter(cardListOnMainFragmentAdapter);
        cardListRecyclerView.scrollToPosition(0);

        cardListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    isCardRecyclerViewIsMoving = false;
                    View centerView = helper.findSnapView(cardLinearLayoutManager);
                    int pos = cardLinearLayoutManager.getPosition(centerView);
                    currentCardIndex = pos;
                } else {
                    isCardRecyclerViewIsMoving = true;
                }
            }
        });

        ImageButton cardDeleteBtn = root.findViewById(R.id.fr_cardlist_deleteBtn);
        cardDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCardRecyclerViewIsMoving) {
                    Toast toast = Toast.makeText(v.getContext(), "명함이 이동 중입니다.", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    System.out.println(currentCardIndex);
                }
            }
        });
        Button cardShareBtn = root.findViewById(R.id.fr_cardlist_shareWithBtn);
        cardShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCardRecyclerViewIsMoving) {
                    Toast toast = Toast.makeText(v.getContext(), "명함이 이동 중입니다.", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    Intent cardShareIntent = new Intent(v.getContext(), CardShareQRcodeActivity.class);
                    cardShareIntent.putExtra("cardId", currentCardIndex);
                    v.getContext().startActivity(cardShareIntent);
                }
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
