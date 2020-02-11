package com.example.tft_stat_checker_native.View;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.tft_stat_checker_native.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ActivityMain extends FragmentActivity {
    private LinearLayout navContentContainer;
    private BottomNavigationView bottomNav;

    private FragmentSearchSummoner searchSummoner;
    private FragmentItemViewer itemViewer;
    private FragmentUnitViewer unitViewer;

    private Fragment activeFragment;
    private int activeFragmentID;

    private static final String SEARCH_SUMMONER_FRAG_ID = "SEARCH_SUMMONER_FRAG_ID";
    private static final String ITEM_VIEWER_FRAG_ID = "ITEM_VIEWER_FRAG_ID";
    private static final String UNIT_VIEWER_FRAG_ID = "UNIT_VIEWER_FRAG_ID";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iniPageComponents();
        iniBottomNav();
        initialNavPage();
    }

    private void iniPageComponents() {
        this.navContentContainer = findViewById(R.id.nav_content_container);
        this.bottomNav = findViewById(R.id.bottom_nav);
    }

    private void initialNavPage() {
        searchSummoner = FragmentSearchSummoner.createInstance();
        activeFragment = searchSummoner;
        activeFragmentID = R.id.search_summoner;
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.nav_content_container, searchSummoner)
                .show(searchSummoner)
                .commit();
    }

    private void addAndShowFragment(Fragment fragment, String tag) {
        if (activeFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.fragment_transition_in, R.anim.fragment_transition_out, R.anim.fragment_transition_in, R.anim.fragment_transition_out)
                    .add(R.id.nav_content_container, fragment)
                    .hide(activeFragment)
                    .show(fragment)
                    .commit();
        }
    }

    private void showFragment(Fragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fragment_transition_in, R.anim.fragment_transition_out, R.anim.fragment_transition_in, R.anim.fragment_transition_out)
                .hide(activeFragment)
                .show(fragment)
                .commit();
    }

    private void iniBottomNav() {
        // swap to selected frag on click
        // if frag is not yet created
        // create, add, and swap to frag
        bottomNav.setOnNavigationItemSelectedListener((menuItem) -> {
            // ignore page reselect
            // reselect is handled below
            if (activeFragmentID == menuItem.getItemId()) {
                return false;
            }
            switch(menuItem.getItemId()) {
                case R.id.search_summoner: {
                    if (searchSummoner == null) {
                        searchSummoner = FragmentSearchSummoner.createInstance();
                        addAndShowFragment(searchSummoner, SEARCH_SUMMONER_FRAG_ID);
                    } else {
                        showFragment(searchSummoner, SEARCH_SUMMONER_FRAG_ID);
                    }
                    activeFragmentID = R.id.search_summoner;
                    activeFragment = searchSummoner;
                    return true;
                }
                case R.id.item_viewer: {
                    if (itemViewer == null) {
                        itemViewer = FragmentItemViewer.createInstance();
                        addAndShowFragment(itemViewer, ITEM_VIEWER_FRAG_ID);
                    } else {
                        showFragment(itemViewer, ITEM_VIEWER_FRAG_ID);
                    }
                    activeFragmentID = R.id.item_viewer;
                    activeFragment = itemViewer;
                    return true;
                }
                case R.id.unit_viewer: {
                    if (unitViewer == null) {
                        unitViewer = FragmentUnitViewer.createInstance();
                        addAndShowFragment(unitViewer, UNIT_VIEWER_FRAG_ID);
                    } else {
                        showFragment(unitViewer, UNIT_VIEWER_FRAG_ID);
                    }
                    activeFragmentID = R.id.unit_viewer;
                    activeFragment = unitViewer;
                    return true;
                }
            }
            return false;
        });

        // handle page reselected
        bottomNav.setOnNavigationItemReselectedListener((menuItem) -> {
            switch(menuItem.getItemId()) {
                case R.id.search_summoner: { searchSummoner.onReselect(); break; }
                case R.id.item_viewer: { itemViewer.onReselect(); break; }
                case R.id.unit_viewer: { unitViewer.onReselect(); break; }
            }
        });

        // update page indicator when pressing
        // back button to navigate between pages
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {

            }
        });
    }
}
