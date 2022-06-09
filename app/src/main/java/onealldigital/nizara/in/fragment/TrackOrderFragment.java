package onealldigital.nizara.in.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import onealldigital.nizara.in.R;
import onealldigital.nizara.in.activity.MainActivity;
import onealldigital.nizara.in.adapter.TrackOrderAdapter;
import onealldigital.nizara.in.adapter.TrackerAdapter;
import onealldigital.nizara.in.model.CurrentOrder;

public class TrackOrderFragment extends Fragment {

    private TabLayout tab;
    private ViewPager2 viewPager;
    private String[] tabTitles =  {"Current Order", "Order History"};
    private TrackOrderAdapter adapter;
    Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_track_order, container, false);

        tab = view.findViewById(R.id.tabs_layout);
        viewPager = view.findViewById(R.id.view_pager);

        setHasOptionsMenu(true);

        adapter = new TrackOrderAdapter(requireActivity());

        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tab, viewPager, (tab, position) -> {
            tab.setText(tabTitles[position]);
        }).attach();

        return view;
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.toolbar_sort).setVisible(true);
        menu.findItem(R.id.toolbar_cart).setVisible(true);
        menu.findItem(R.id.toolbar_search).setVisible(false);
        menu.findItem(R.id.toolbar_notification).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.showHideSearchBar(false);
    }
}