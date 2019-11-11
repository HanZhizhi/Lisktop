package com.space.lisktop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.Preference.OnPreferenceChangeListener;

import com.space.lisktop.R;


public class SettingsActivity extends AppCompatActivity {
    private Button btChooseMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initViews();
    }

    private void initViews()
    {
        btChooseMain=findViewById(R.id.bt_settings_chooseMain);
        btChooseMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this,ChooseMainActivity.class));
            }
        });
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements OnPreferenceChangeListener{
        //private SharedPreferences sPref= getActivity().getPreferences(Context.MODE_WORLD_WRITEABLE);

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.pref_settings, rootKey);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            //final SharedPreferences sPref= PreferenceManager.getDefaultSharedPreferences(getActivity());
//            final boolean showicon=sPref.getBoolean("showicon",false);
//
//            final Preference prefShowIcon=findPreference("showicon");
//            //prefShowIcon.setDefaultValue(showicon);
//            prefShowIcon.setOnPreferenceChangeListener(this);
            super.onViewCreated(view, savedInstanceState);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            Log.i("prefChg",preference.getKey()+preference.toString()+"--"+newValue.toString());
//            switch (preference.getKey())
//            {
//                case "showicon":
//                    sPref.edit().putBoolean("showicon",(boolean)newValue).apply();
//                    break;
//            }
            return false;
        }
    }
}