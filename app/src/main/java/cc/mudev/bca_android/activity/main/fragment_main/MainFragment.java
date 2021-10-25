package cc.mudev.bca_android.activity.main.fragment_main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import cc.mudev.bca_android.R;
import cc.mudev.bca_android.activity.main.fragment_main.tab_cardlist.CardListFragment;
import cc.mudev.bca_android.activity.main.fragment_main.tab_chatlist.ChatListFragment;
import cc.mudev.bca_android.activity.main.fragment_main.tab_profilelist.ProfileListFragment;
import cc.mudev.bca_android.databinding.FragmentMainBinding;

public class MainFragment extends Fragment {
    static final int NUM_ITEMS = 3;
    private FragmentMainBinding binding;

    TabLayout tabLayout;
    ViewPager2 viewPager;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        tabLayout = root.findViewById(R.id.fr_main_tabBar);
        viewPager = root.findViewById(R.id.fr_main_tabViewPager2);
        viewPager.setAdapter(new MainFragmentTabAdapter(getChildFragmentManager(), getLifecycle()));
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("프로필");
                    tab.setIcon(R.drawable.ic_home);
                    break;
                case 1:
                    tab.setText("메시지");
                    tab.setIcon(R.drawable.ic_baseline_message);
                    break;
                case 2:
                    tab.setText("명함");
                    tab.setIcon(R.drawable.ic_card_account_details);
                    break;
            }
        }).attach();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 2:
                        viewPager.setUserInputEnabled(false);
                        break;
                    default:
                        viewPager.setUserInputEnabled(true);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 2:
                        viewPager.setUserInputEnabled(false);
                        break;
                    default:
                        viewPager.setUserInputEnabled(true);
                        break;
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

    public static class MainFragmentTabAdapter extends FragmentStateAdapter {
        public MainFragmentTabAdapter(FragmentManager fm, Lifecycle lc) {
            super(fm, lc);
        }

        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new ProfileListFragment();
                case 1:
                    return new ChatListFragment();
                case 2:
                    return new CardListFragment();
                default:
                    throw new AssertionError();
            }
        }

        @Override
        public int getItemCount() {
            return NUM_ITEMS;
        }
    }

}
