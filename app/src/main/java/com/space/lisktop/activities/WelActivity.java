package com.space.lisktop.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.space.lisktop.R;

public class WelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wel);

        SharedPreferences sPref=getSharedPreferences("first_open",MODE_PRIVATE);
        SharedPreferences.Editor editor=sPref.edit();
        editor.putBoolean("first_open",false).commit();

        Intent chooseIntent=new Intent(this, ChooseDockActivity.class);
        startActivity(chooseIntent);
        finish();
    }
}
