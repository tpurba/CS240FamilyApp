package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    //make all the views
    private Switch lifeLineSwitch;
    private Switch familyTreeSwitch;
    private Switch spouseLine;
    private Switch fatherSideSwitch;
    private Switch motherSideSwitch;
    private Switch maleSwitch;
    private Switch femaleSwitch;
    private LinearLayout logOut;
    Datacache instance = Datacache.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //set all the views
        lifeLineSwitch = findViewById(R.id.LifeLineSwitch);
        familyTreeSwitch = findViewById(R.id.FamilyTreeLines);
        spouseLine = findViewById(R.id.SpouseLine);
        fatherSideSwitch = findViewById(R.id.FatherSide);
        motherSideSwitch = findViewById(R.id.MotherSide);
        maleSwitch = findViewById(R.id.MaleSwitch);
        femaleSwitch = findViewById(R.id.FemaleSwitch);
        logOut = findViewById(R.id.LogOut);
        //set the
        boolean settings[] = instance.getSettings();
        //set them as default
        lifeLineSwitch.setChecked(settings[0]);
        familyTreeSwitch.setChecked(settings[1]);
        spouseLine.setChecked(settings[2]);
        fatherSideSwitch.setChecked(settings[3]);
        motherSideSwitch.setChecked(settings[4]);
        maleSwitch.setChecked(settings[5]);
        femaleSwitch.setChecked(settings[6]);
        lifeLineSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isLifeLine) {
                settings[0] = isLifeLine;
                instance.setSettings(settings);
            }
        });
        familyTreeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isFamilyTree) {
                settings[1] = isFamilyTree;
                instance.setSettings(settings);
            }
        });
        spouseLine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isSpouseLine) {
                settings[2] = isSpouseLine;
                instance.setSettings(settings);
            }
        });
        fatherSideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isFatherSide) {
                settings[3] = isFatherSide;
                instance.setSettings(settings);
            }
        });
        motherSideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isMotherSide) {
                settings[4] = isMotherSide;
                instance.setSettings(settings);
            }
        });
       maleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton compoundButton, boolean isMaleSwitch) {
               settings[5] = isMaleSwitch;
               instance.setSettings(settings);
           }
       });
       femaleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton compoundButton, boolean isFemaleSwitch) {
               settings[6] = isFemaleSwitch;
               instance.setSettings(settings);
           }
       });
       logOut.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               logOut();
           }
       });

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
    public void logOut()
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        instance.clearAll();
    }

}