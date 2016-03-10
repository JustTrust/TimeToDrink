package org.belichenko.a.timetodrink.data_structure;

import org.belichenko.a.timetodrink.Constants;

import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.QueryMap;

/**
 * Get weather currentData
 */
public class Retrofit implements Constants {
    private static final String ENDPOINT = "https://maps.googleapis.com/maps/api";
    private static ApiInterface apiInterface;

    static {
        initialize();
    }

    interface ApiInterface {
        @GET("/place/nearbysearch/json")
        void getPlacesData(@QueryMap Map<String,String> filters, Callback<PointsData> callback);
        @GET("/place/nearbysearch/json")
        void getListOfCity(@QueryMap Map<String,String> filters, Callback<PointsData> callback);
    }

    public static void initialize() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setLogLevel(RestAdapter.LogLevel.HEADERS_AND_ARGS)
                .build();
        apiInterface = restAdapter.create(ApiInterface.class);
    }

    public static void getPlacesData(Map<String,String> filters, Callback<PointsData> callback) {
        apiInterface.getPlacesData(filters, callback);
    }
    public static void getListOfCity(Map<String,String> filters, Callback<PointsData> callback) {
        apiInterface.getListOfCity(filters ,callback);
    }
}
