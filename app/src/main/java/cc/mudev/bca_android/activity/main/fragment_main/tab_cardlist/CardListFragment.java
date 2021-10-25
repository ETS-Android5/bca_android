package cc.mudev.bca_android.activity.main.fragment_main.tab_cardlist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Network;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import java.util.ArrayList;
import java.util.List;

import cc.mudev.bca_android.R;
import cc.mudev.bca_android.activity.card.CardCreateActivity;
import cc.mudev.bca_android.activity.card.CardShareQRcodeActivity;
import cc.mudev.bca_android.activity.core.CoreFrontActivity;
import cc.mudev.bca_android.activity.main.MainActivity;
import cc.mudev.bca_android.adapter.CardListOnMainFragmentAdapter;
import cc.mudev.bca_android.adapter.ProfileDetailCardListData;
import cc.mudev.bca_android.database.ProfileDatabase;
import cc.mudev.bca_android.database.TB_CARD;
import cc.mudev.bca_android.databinding.FragmentMainTabCardlistBinding;
import cc.mudev.bca_android.network.BCaAPI.AccountAPI;
import cc.mudev.bca_android.network.BCaAPI.CardAPI;
import cc.mudev.bca_android.network.NetworkSupport;
import cc.mudev.bca_android.util.AlertDialogGenerator;

public class CardListFragment extends Fragment {
    private static final String ERROR_DIALOG_TITLE = "명함 목록에 문제가 발생했습니다";

    private FragmentMainTabCardlistBinding binding;
    ArrayList<ProfileDetailCardListData> cardDataList;
    public int currentCardIndex = 0;
    public boolean isCardRecyclerViewIsMoving = false;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainTabCardlistBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        FragmentActivity parentFragmentActivity = getParentFragment().getActivity();
        Context appContext = root.getContext();

        Toolbar toolbar = root.findViewById(R.id.fr_cardlist_toolbar);
        toolbar.setNavigationOnClickListener((view) -> ((DrawerLayout) parentFragmentActivity.findViewById(R.id.main_drawerLayout)).openDrawer(GravityCompat.START));
        toolbar.setOnMenuItemClickListener((item) -> {
            if (item.getItemId() == R.id.to_cardList_createCardBtn) {// Goto chat room create activity
                Intent cardCreateIntent = new Intent(appContext, CardCreateActivity.class);
                appContext.startActivity(cardCreateIntent);
            }
            return true;
        });

        NetworkSupport api = NetworkSupport.getInstance(appContext);
        int profileId = api.getCurrentProfileId();
        if (profileId < 0) {
            AlertDialogGenerator.gen(
                    appContext,
                    ERROR_DIALOG_TITLE, "명함 목록을 불러오는 중 문제가 발생했습니다,\n다시 로그인 해 주세요.",
                    false, "확인", (d, i) -> {
                        AccountAPI.signout(appContext);
                        Intent frontIntent = new Intent(appContext, CoreFrontActivity.class);
                        frontIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        appContext.startActivity(frontIntent);
                        getActivity().finishAffinity();
                    });
        }

        cardDataList = new ArrayList<>();
        List<TB_CARD> cardDBList = ProfileDatabase.getInstance(appContext).cardDao().loadByProfileID(profileId);
        for (TB_CARD cardDBData : cardDBList) {
            cardDataList.add(new ProfileDetailCardListData(cardDBData.uuid, cardDBData.name, cardDBData.preview_url, (cardDBData.is_private > 0) ? true : false));
        }

        if (cardDataList.isEmpty()) {
            cardDataList.add(new ProfileDetailCardListData(-1, "", "", false));
        }

        Point size = new Point();
        parentFragmentActivity.getWindowManager().getDefaultDisplay().getSize(size);
        int width = size.x;
        int cardWidthDpAsPixels = (int) ((225 + 16) * getResources().getDisplayMetrics().density + 0.5f);

        RecyclerView cardListRecyclerView = root.findViewById(R.id.fr_cardlist_cardRecyclerView);
        int paddingStartEnd = (width - cardWidthDpAsPixels) / 2;
        cardListRecyclerView.setPadding(paddingStartEnd, 0, paddingStartEnd, 0);

        RecyclerView.LayoutManager cardLinearLayoutManager = new LinearLayoutManager(appContext, LinearLayoutManager.HORIZONTAL, false);

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
        cardDeleteBtn.setOnClickListener((v) -> {
            if (isCardRecyclerViewIsMoving) {
                Toast.makeText(appContext, "명함이 이동 중입니다.", Toast.LENGTH_LONG).show();
                return;
            }
            CardAPI.delete(appContext, getCurrentPositionCardId());
        });

        Button cardShareBtn = root.findViewById(R.id.fr_cardlist_shareWithBtn);
        cardShareBtn.setOnClickListener((v) -> {
            if (isCardRecyclerViewIsMoving) {
                Toast.makeText(appContext, "명함이 이동 중입니다.", Toast.LENGTH_LONG).show();
                return;
            }

            Intent cardShareIntent = new Intent(appContext, CardShareQRcodeActivity.class);
            cardShareIntent.putExtra("cardId", getCurrentPositionCardId());
            appContext.startActivity(cardShareIntent);
        });

        return root;
    }

    public int getCurrentPositionCardId() {
        try {
            return this.cardDataList.get(this.currentCardIndex).cardId;
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
