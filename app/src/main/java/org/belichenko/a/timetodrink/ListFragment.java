package org.belichenko.a.timetodrink;

import android.content.Context;
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
import org.belichenko.a.timetodrink.data_structure.Retrofit;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ListFragment extends Fragment {

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
        LinkedHashMap<String, String> filter = new LinkedHashMap<>();
        filter.put("location", String.format("%.7f,%.7f", location.getLatitude(), location.getLongitude()));
        filter.put("radius", "1500");
        filter.put("language", "ru");
        //filter.put("rankby", "distance");
        filter.put("types", "bar|liquor_store");
        filter.put("key", "AIzaSyC9JgNsRuDi0j5gUoE4WOwRZ7LrV85NPXA");

        Retrofit.getPlacesData(filter, new Callback<PointsData>() {
            @Override
            public void success(PointsData pointsData, Response response) {
                if (pointsData != null) {
                    if (pointsData.status.equals("OK")) {
                        if (pointsData.results != null) {
                            resultsArrayList.clear();
                            resultsArrayList.addAll(pointsData.results);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        resultsArrayList.clear();
                        resultsArrayList.add(new Results(pointsData.status));
                        resultsArrayList.add(new Results(pointsData.error_message));
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                resultsArrayList.clear();
                resultsArrayList.add(new Results(error.toString()));
                adapter.notifyDataSetChanged();
            }
        });
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
