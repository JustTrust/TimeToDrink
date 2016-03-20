package org.belichenko.a.timetodrink;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.belichenko.a.timetodrink.data_details.DetailsData;
import org.belichenko.a.timetodrink.data_structure.Results;
import org.belichenko.a.timetodrink.data_structure.googleNearbyPlaces;

import java.util.LinkedHashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
        implements Constants
        , Callback<DetailsData>
        , ListFragment.OnListFragmentInteractionListener
        , GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener
        , LocationListener {

    private static final String TAG = "Main activity";
    private SectionsPagerAdapter mSectionsPagerAdapter;
    protected Location currentLocation;
    private ViewPager mViewPager;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setCollapsible(true);
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        buildGoogleApiClient();
        createLocationRequest();
        checkSetting();
    }

    private void checkSetting() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates locationSettings = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.d(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    private void buildGoogleApiClient() {
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    protected void createLocationRequest() {
        SharedPreferences sharedPref = getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(sharedPref.getInt(UPDATE_TIME, TEN_SECONDS));
        mLocationRequest.setFastestInterval(FIVE_SECONDS);
        mLocationRequest.setPriority(sharedPref.getInt(ACCURACY, LocationRequest.PRIORITY_HIGH_ACCURACY));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        break;
                }
                break;
        }

        Log.d(TAG, "onActivityResult() called with: "
                + "requestCode = [" + requestCode + "], resultCode = ["
                + resultCode + "], data = [" + data + "]");
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this
                , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this
                , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                Log.d(TAG, "startLocationUpdates() called with: " + "status = [" + status + "]");
            }
        });

    }

    @Override
    public void onListFragmentInteraction(Results item) {
        Log.d(TAG, "onListFragmentInteraction() called with: " + "item = [" + item + "]");
        if (item.geometry != null) {
            BarMapFragment.getInstance().makePoint(item);
            mViewPager.setCurrentItem(1);
        }
    }

    @Override
    public void onLongListClick(Results item) {
        Log.d(TAG, "onLongListClick() called with: " + "item = [" + item + "]");
        if (!item.place_id.isEmpty()) {
            showDetailInformation(item.place_id);
        }
    }

    private void showDetailInformation(String place_id) {

        LinkedHashMap<String, String> filter = new LinkedHashMap<>();
        filter.put("placeid", place_id);
        filter.put("language", "ru");
        filter.put("key", getString(R.string.google_maps_web_key));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // prepare call in Retrofit 2.0
        googleNearbyPlaces detailData = retrofit.create(googleNearbyPlaces.class);

        Call<DetailsData> call = detailData.getDetailData(filter);
        //asynchronous call
        call.enqueue(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        } else {
            Log.d(TAG, "onResume() mGoogleApiClient not connected");
        }
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        } else {
            Log.d(TAG, "stopLocationUpdates() mGoogleApiClient not connected");
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this
                , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this
                , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            currentLocation = mLastLocation;
            BarMapFragment.getInstance().updateCurrentPlace(currentLocation);
            ListFragment.getInstance().updatePlaces(currentLocation);
        }
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        BarMapFragment.getInstance().updateCurrentPlace(currentLocation);
        ListFragment.getInstance().updatePlaces(currentLocation);
    }

    @Override
    public void onResponse(Call<DetailsData> call, Response<DetailsData> response) {
        if (response.body() == null) {
            return;
        }
        if (!response.body().status.equals("OK")){
            Toast.makeText(MainActivity.this, response.body().status, Toast.LENGTH_LONG).show();
        }
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View detailView = layoutInflater.inflate(R.layout.details_data, null);

        TextView text_detail_name = (TextView) detailView.findViewById(R.id.text_detail_name);
        text_detail_name.setText(response.body().result.name);
        TextView text_detail_adress = (TextView) detailView.findViewById(R.id.text_detail_adress);
        text_detail_adress.setText(response.body().result.formatted_address);
        TextView text_detail_phone = (TextView) detailView.findViewById(R.id.text_detail_phone);
        text_detail_phone.setText(response.body().result.formatted_phone_number);
        TextView text_detail_website = (TextView) detailView.findViewById(R.id.text_detail_website);
        text_detail_website.setText(response.body().result.website);
        RatingBar ratingBar = (RatingBar) detailView.findViewById(R.id.ratingBar);
        ratingBar.setRating(response.body().result.rating);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert
                .setTitle(R.string.detailInformation)
                .setView(detailView)
                .show();
    }

    @Override
    public void onFailure(Call<DetailsData> call, Throwable t) {

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ListFragment.getInstance();
                case 1:
                    return BarMapFragment.getInstance();
                case 2:
                    return SettingFragment.getInstance();
            }
            return null;
        }

//        @Override
//        public int getItemPosition(Object object) {
//            return POSITION_NONE;
//        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return ListFragment.NAME;
                case 1:
                    return BarMapFragment.NAME;
                case 2:
                    return SettingFragment.NAME;
            }
            return null;
        }
    }
}