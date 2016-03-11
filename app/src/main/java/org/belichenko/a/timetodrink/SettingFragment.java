package org.belichenko.a.timetodrink;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.location.LocationRequest;

import butterknife.Bind;
import butterknife.ButterKnife;


public class SettingFragment extends Fragment implements Constants {

    @Bind(R.id.rg_update_time)
    RadioGroup rgUpdateTime;
    @Bind(R.id.rg_radius)
    RadioGroup rgRadius;
    @Bind(R.id.rg_accuracy)
    RadioGroup rgAccuracy;

    private static SettingFragment ourInstance = new SettingFragment();
    public static final String NAME = "Settings";

    public static SettingFragment getInstance() {
        return ourInstance;
    }

    public SettingFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, rootView);
        readSettingsFromPreferences();
        setListeners();
        return rootView;
    }

    private void setListeners() {

        rgRadius.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences mPrefs = App.getAppContext()
                        .getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = mPrefs.edit();
                RadioButton rb = (RadioButton) rgRadius.findViewById(checkedId);
                if (rb != null) {
                    edit.putString(STORED_RADIUS, rb.getText().toString());
                    if (rb.getText().toString().equals(getString(R.string.text_radius1))) {
                        edit.putString(RADIUS, "1000");
                    }else{
                        edit.putString(RADIUS, "3000");
                    }
                    edit.apply();
                }
            }
        });

        rgAccuracy.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences mPrefs = App.getAppContext()
                        .getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = mPrefs.edit();
                RadioButton rb = (RadioButton) rgAccuracy.findViewById(checkedId);
                if (rb.getText().toString().equals(getString(R.string.text_accuracy1))) {
                    edit.putInt(ACCURACY, LocationRequest.PRIORITY_HIGH_ACCURACY);
                }else{
                    edit.putInt(ACCURACY, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                }

                if (rb != null) {
                    edit.putString(STORED_ACCURACY, rb.getText().toString());
                    edit.apply();
                }
            }
        });

        rgUpdateTime.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences mPrefs = App.getAppContext()
                        .getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = mPrefs.edit();
                RadioButton rb = (RadioButton) rgUpdateTime.findViewById(checkedId);
                if (rb.getText().toString().equals(getString(R.string.text_time1))) {
                    edit.putInt(UPDATE_TIME, TEN_SECONDS);
                }else{
                    edit.putInt(UPDATE_TIME, TWENTY_SECONDS);
                }

                if (rb != null) {
                    edit.putString(STORED_UPDATE_TIME, rb.getText().toString());
                    edit.apply();
                }
            }
        });
    }

    private void readSettingsFromPreferences() {

        SharedPreferences mPrefs = App.getAppContext()
                .getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE);

        String nameRadius = mPrefs.getString(STORED_RADIUS, getString(R.string.text_radius1));
        RadioButton rbRadius;
        if (nameRadius.equals(getString(R.string.text_radius1))) {
            rbRadius = (RadioButton) rgRadius.getChildAt(0);
        } else {
            rbRadius = (RadioButton) rgRadius.getChildAt(1);
        }
        if (rbRadius != null) {
            rbRadius.setChecked(true);
        }

        String nameAccuracy = mPrefs.getString(STORED_ACCURACY, getString(R.string.text_accuracy1));
        RadioButton rbAccuracy;
        if (nameAccuracy.equals(getString(R.string.text_accuracy1))) {
            rbAccuracy = (RadioButton) rgAccuracy.getChildAt(0);
        } else {
            rbAccuracy = (RadioButton) rgAccuracy.getChildAt(1);
        }
        if (rbAccuracy != null) {
            rbAccuracy.setChecked(true);
        }

        String nameTime = mPrefs.getString(STORED_UPDATE_TIME, getString(R.string.text_time1));
        RadioButton rbTime;
        if (nameTime.equals(getString(R.string.text_time1))) {
            rbTime = (RadioButton) rgUpdateTime.getChildAt(0);
        } else {
            rbTime = (RadioButton) rgUpdateTime.getChildAt(1);
        }
        if (rbTime != null) {
            rbTime.setChecked(true);
        }
    }
}
