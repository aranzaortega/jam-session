package com.aios.jamsession.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.aios.jamsession.R;
import com.aios.jamsession.fragments.ChatFragment;
import com.aios.jamsession.fragments.HomeFragment;
import com.aios.jamsession.fragments.ProfileFragment;
import com.aios.jamsession.fragments.SearchFragment;
import com.aios.jamsession.providers.AuthProvider;
import com.aios.jamsession.providers.TokenProvider;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    // Variables
    BottomNavigationView bottomNavigation;

    TokenProvider mTokenProvider;
    AuthProvider mAuthProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Instance
        bottomNavigation = findViewById(R.id.bottom_navigation);
        mTokenProvider = new TokenProvider();
        mAuthProvider = new AuthProvider();

        // Events
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        // Default fragment
        openFragment(new HomeFragment());
        createToken();
    }

    // To open other fragments
    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // Menu controller
    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
        new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        openFragment(new HomeFragment());
                        return true;
                    case R.id.navigation_search:
                        openFragment(new SearchFragment());
                        return true;
                    case R.id.navigation_chats:
                        openFragment(new ChatFragment());
                        return true;
                    case R.id.navigation_profile:
                        openFragment(new ProfileFragment());
                        return true;
                }
                return false;
            }
        };

    private void createToken(){
        mTokenProvider.create(mAuthProvider.getUserId());
    }
}