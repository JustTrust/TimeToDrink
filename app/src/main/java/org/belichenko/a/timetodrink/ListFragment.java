package org.belichenko.a.timetodrink;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.belichenko.a.timetodrink.data_structure.PointsData;
import org.belichenko.a.timetodrink.data_structure.Results;
import org.belichenko.a.timetodrink.data_structure.googleNearbyPlaces;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListFragment extends Fragment implements Callback<PointsData>, Constants {

    private OnListFragmentInteractionListener mListener;
    private static final String TAG = "List Fragment ";
    private static ListFragment fragment = new ListFragment();
    public static final String NAME = "Bars";
    private static ArrayList<Results> resultsArrayList;
    private static PointsRecyclerViewAdapter adapter;

    public ListFragment() {
    }

    public static ListFragment getInstance() {
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        resultsArrayList = new ArrayList<>();
        // Set the adapter
        if (view instanceof RecyclerView) {
            adapter = new PointsRecyclerViewAdapter(resultsArrayList, mListener);
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(App.getAppContext()));
            recyclerView.setAdapter(adapter);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    protected void updatePlaces(Location location) {

        SharedPreferences sharedPref = App.getAppContext()
                .getSharedPreferences(STORAGE_OF_SETTINGS, Context.MODE_PRIVATE);

        LinkedHashMap<String, String> filter = new LinkedHashMap<>();
        filter.put("location", String.valueOf(location.getLatitude()) + ","
                + String.valueOf(location.getLongitude()));
        filter.put("radius", sharedPref.getString(RADIUS, "1000"));
        filter.put("language", "ru");
        filter.put("types", "bar|liquor_store");
        filter.put("key", "AIzaSyC9JgNsRuDi0j5gUoE4WOwRZ7LrV85NPXA");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // prepare call in Retrofit 2.0
        googleNearbyPlaces stackOverflowAPI = retrofit.create(googleNearbyPlaces.class);

        Call<PointsData> call = stackOverflowAPI.getPlacesData(filter);
        //asynchronous call
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<PointsData> call, Response<PointsData> response) {
        if (response.body() != null) {
            if (response.body().status.equals("OK")) {
                if (response.body().results != null) {
                    resultsArrayList.clear();
                    resultsArrayList.addAll(response.body().results);
                    adapter.notifyDataSetChanged();
                }
            } else {
                resultsArrayList.clear();
                resultsArrayList.add(new Results(response.body().status));
                resultsArrayList.add(new Results(response.body().error_message));
                adapter.notifyDataSetChanged();
            }
        }

    }

    @Override
    public void onFailure(Call<PointsData> call, Throwable t) {
        resultsArrayList.clear();
        resultsArrayList.add(new Results(t.getLocalizedMessage()));
        adapter.notifyDataSetChanged();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Results item);
    }
}
