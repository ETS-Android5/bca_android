package cc.mudev.bca_android.activity.main.fragment_main.tab_profilelist;

import android.app.Activity;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import cc.mudev.bca_android.R;
import cc.mudev.bca_android.activity.profile.ProfileDetailActivity;
import cc.mudev.bca_android.adapter.ProfileFollowingData;
import cc.mudev.bca_android.adapter.ProfileFollowingAdapter;
import cc.mudev.bca_android.databinding.FragmentMainTabProfilelistBinding;

public class ProfileListFragment extends Fragment {
    private FragmentMainTabProfilelistBinding binding;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainTabProfilelistBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Toolbar toolbar = root.findViewById(R.id.fr_profileList_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = getParentFragment().getActivity().findViewById(R.id.main_drawerLayout);
                drawer.openDrawer(GravityCompat.START);
            }
        });

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
        profileFollowingList.add(new ProfileFollowingData("김보라", "개발2팀"));
        profileFollowingList.add(new ProfileFollowingData("김환경", ""));
        profileFollowingList.add(new ProfileFollowingData("김희망", "영업팀"));
        profileFollowingList.add(new ProfileFollowingData("나다영", "R&D 연구팀"));
        profileFollowingList.add(new ProfileFollowingData("잔디", "환경 아티스트"));
        profileFollowingList.add(new ProfileFollowingData("박기수", "R&D 연구팀"));
        profileFollowingList.add(new ProfileFollowingData("임한울", ""));
        profileFollowingList.add(new ProfileFollowingData("이용", "성진개발 영업팀"));
        profileFollowingList.add(new ProfileFollowingData("안치홓", "서버 유지보수"));
        profileFollowingList.add(new ProfileFollowingData("김보라", "개발2팀"));
        profileFollowingList.add(new ProfileFollowingData("김환경", ""));
        profileFollowingList.add(new ProfileFollowingData("김희망", "영업팀"));
        profileFollowingList.add(new ProfileFollowingData("나다영", "R&D 연구팀"));
        profileFollowingList.add(new ProfileFollowingData("잔디", "환경 아티스트"));
        profileFollowingList.add(new ProfileFollowingData("박기수", "R&D 연구팀"));
        profileFollowingList.add(new ProfileFollowingData("임한울", ""));
        profileFollowingList.add(new ProfileFollowingData("이용", "성진개발 영업팀"));
        profileFollowingList.add(new ProfileFollowingData("안치홓", "서버 유지보수"));
        profileFollowingList.add(new ProfileFollowingData("김보라", "개발2팀"));
        profileFollowingList.add(new ProfileFollowingData("김환경", ""));
        profileFollowingList.add(new ProfileFollowingData("김희망", "영업팀"));
        profileFollowingList.add(new ProfileFollowingData("나다영", "R&D 연구팀"));
        profileFollowingList.add(new ProfileFollowingData("잔디", "환경 아티스트"));
        profileFollowingList.add(new ProfileFollowingData("박기수", "R&D 연구팀"));
        profileFollowingList.add(new ProfileFollowingData("임한울", ""));
        profileFollowingList.add(new ProfileFollowingData("이용", "성진개발 영업팀"));
        profileFollowingList.add(new ProfileFollowingData("안치홓", "서버 유지보수"));

        RecyclerView recyclerView = root.findViewById(R.id.fr_profileList_profileRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));

        ProfileFollowingAdapter adapter = new ProfileFollowingAdapter(profileFollowingList);
        adapter.setOnItemClickListener(new ProfileFollowingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent myProfileIntent = new Intent(view.getContext(), ProfileDetailActivity.class);

                ImageView profileImage = view.findViewById(R.id.li_profileList_profileImg);
                profileImage.setTransitionName("profileImage");
                Pair<View, String> pair_thumb = Pair.create(profileImage, profileImage.getTransitionName());
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) view.getContext(), pair_thumb);

                view.getContext().startActivity(myProfileIntent, optionsCompat.toBundle());
                profileImage.setTransitionName(null);
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
