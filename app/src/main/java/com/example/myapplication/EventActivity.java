package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public class EventActivity extends AppCompatActivity {
//handle the calling of the fragment just like in main activity
    final String KEY = "EVENTID";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = new Bundle();
        Intent intent = getIntent();
        String eventId = (String)intent.getExtras().get(KEY);
        bundle.putString("EVENTID", eventId);
        setContentView(R.layout.activity_event);
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment mapFragment = new MapFragment();
        mapFragment.setArguments(bundle);
        fragmentManager.beginTransaction().add(R.id.fragmentFrameLayout, mapFragment).commit();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }
}