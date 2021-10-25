package cc.mudev.bca_android.activity.main.fragment_main.tab_profilelist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import cc.mudev.bca_android.R;
import cc.mudev.bca_android.activity.profile.ProfileDetailActivity;
import cc.mudev.bca_android.adapter.ProfileFollowingAdapter;
import cc.mudev.bca_android.databinding.FragmentMainTabProfilelistBinding;

public class ProfileListFragment extends Fragment {
    private FragmentMainTabProfilelistBinding binding;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainTabProfilelistBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        FragmentActivity parentFragmentActivity = getParentFragment().getActivity();
        Context appContext = root.getContext();

        Toolbar toolbar = root.findViewById(R.id.fr_profileList_toolbar);
        toolbar.setNavigationOnClickListener((view) -> ((DrawerLayout) parentFragmentActivity.findViewById(R.id.main_drawerLayout)).openDrawer(GravityCompat.START));

        RecyclerView recyclerView = root.findViewById(R.id.fr_profileList_profileRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(appContext));
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        ProfileFollowingAdapter adapter = new ProfileFollowingAdapter();
        adapter.setOnItemClickListener((view, position) -> {
            Intent myProfileIntent = new Intent(appContext, ProfileDetailActivity.class);

            ImageView profileImage = view.findViewById(R.id.li_profileList_profileImg);
            profileImage.setTransitionName("profileImage");
            Pair<View, String> pair_thumb = Pair.create(profileImage, profileImage.getTransitionName());
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) appContext, pair_thumb);

            myProfileIntent.putExtra("profileId", adapter.infoListData.get(position).profileId);
            appContext.startActivity(myProfileIntent, optionsCompat.toBundle());
            profileImage.setTransitionName(null);
        });
        recyclerView.setAdapter(adapter);
        adapter.refreshData(appContext);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
