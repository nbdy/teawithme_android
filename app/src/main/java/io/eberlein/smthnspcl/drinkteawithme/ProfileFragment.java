package io.eberlein.smthnspcl.drinkteawithme;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFragment extends Fragment {

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    private Activity context;


    public ProfileFragment(Activity ctx) {
        context = ctx;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, v);
        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.content, new InfoFragment()).commit();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String s = tab.getText().toString();
                Fragment f = null;
                if (s.equals(context.getString(R.string.info))) {
                    f = new InfoFragment();
                } else if (s.equals(context.getString(R.string.sessions))) {
                    f = new SessionsFragment();
                } else if (s.equals(context.getString(R.string.statistics))) {
                    f = new StatisticsFragment();
                }
                if (f != null)
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.content, f).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return v;
    }
}
