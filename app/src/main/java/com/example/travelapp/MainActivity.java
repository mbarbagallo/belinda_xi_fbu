package com.example.travelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.travelapp.fragments.ComposeFragment;
import com.example.travelapp.fragments.HomeFragment;
import com.example.travelapp.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    private Button btnLogOut;
    private ProgressBar pbLoading;
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.bottomNavigationView = findViewById(R.id.bottom_navigation);
        this.pbLoading = findViewById(R.id.pbLoading);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_compose:
                        Toast.makeText(MainActivity.this, "add", Toast.LENGTH_SHORT).show();
                        fragment = new ComposeFragment();
                        break;
                    case R.id.action_home:
                        Toast.makeText(MainActivity.this, "home", Toast.LENGTH_SHORT).show();
                        fragment = new HomeFragment();
                        break;
                    case R.id.action_profile:
                        Toast.makeText(MainActivity.this, "profile", Toast.LENGTH_SHORT).show();
                        fragment = new ProfileFragment();
                        break;
                    default:
                        // TODO - change fragment
                        fragment = new HomeFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_home);

    }

    public void showProgressBar() {
        pbLoading.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        pbLoading.setVisibility(View.INVISIBLE);
    }
}