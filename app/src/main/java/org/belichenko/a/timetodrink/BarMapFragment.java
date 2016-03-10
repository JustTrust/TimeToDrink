package org.belichenko.a.timetodrink;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.belichenko.a.timetodrink.data_structure.Results;


public class BarMapFragment extends Fragment implements OnMapReadyCallback {

    private static BarMapFragment ourInstance = new BarMapFragment();
    public static final String NAME = "Map";
    private SupportMapFragment supportMapFragment;
    private GoogleMap mMap;
    Marker currentPositionMarker;

    public static BarMapFragment getInstance() {
        return ourInstance;
    }

    public BarMapFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        if (supportMapFragment == null) {
            supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        }
        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(this);
        }
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null) {
            mMap = googleMap;
        }
    }

    protected void updateCurrentPlace(Location location) {
        final LatLng currentMarker = new LatLng(location.getLatitude(), location.getLongitude());
        if (currentPositionMarker != null) {
            currentPositionMarker.remove();
            currentPositionMarker = mMap.addMarker(new MarkerOptions()
                    .position(currentMarker).flat(false).draggable(true).title(getString(R.string.current_lacation)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentMarker));
        }else{
            currentPositionMarker = mMap.addMarker(new MarkerOptions()
                    .position(currentMarker).flat(false).draggable(true).title(getString(R.string.current_lacation)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentMarker, 15));
        }
    }

    protected void makePoint(Results point) {
        if (currentPositionMarker != null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions()
                    .position(currentPositionMarker.getPosition())
                    .flat(true)
                    .draggable(true)
                    .title(currentPositionMarker.getTitle())
                    .snippet(currentPositionMarker.getSnippet()));
        }
        if (point != null) {
            ImageView imageView = new ImageView(App.getAppContext());
            Picasso.with(App.getAppContext())
                    .load(point.icon)
                    .error(R.mipmap.ic_launcher)
                    .into(imageView);
            final LatLng coordinates = new LatLng(point.geometry.location.lat, point.geometry.location.lng);
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(coordinates)
                    .flat(true)
                    .draggable(true)
                    .title(point.name)
                    .snippet(point.vicinity)
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 11));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 1200, null);
        }
    }
}
