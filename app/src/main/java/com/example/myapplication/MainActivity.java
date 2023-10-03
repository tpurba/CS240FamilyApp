package com.example.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

public class MainActivity extends AppCompatActivity implements LoginFragment.Listener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentFrameLayout);
        if(fragment == null)
        {
            fragment = createLoginFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragmentFrameLayout, fragment)
                    .commit();
        }
        else
        {
            if(fragment instanceof LoginFragment)
            {
                ((LoginFragment) fragment).registerListener(this);
            }
        }
        Iconify.with(new FontAwesomeModule());
    }
    private Fragment createLoginFragment()
    {
        LoginFragment fragment = new LoginFragment();
        fragment.registerListener(this);
        return fragment;
    }
    @Override
    public void notifyDone()
    {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment mapFragment = new MapFragment();
        fragmentManager.beginTransaction().replace(R.id.fragmentFrameLayout, mapFragment).commit();
    }

}