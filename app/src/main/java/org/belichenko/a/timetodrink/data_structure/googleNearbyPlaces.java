package org.belichenko.a.timetodrink.data_structure;

import org.belichenko.a.timetodrink.data_details.DetailsData;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Get nearby places
 */
public interface googleNearbyPlaces {
    @GET("/maps/api/place/nearbysearch/json")
    Call<PointsData> getPlacesData(@QueryMap Map<String,String> filters);

    @GET("/maps/api/place/details/json")
    Call<DetailsData> getDetailData(@QueryMap Map<String,String> filters);

    @GET("/maps/api/place/details/json")
    Call<DetailsData> getPhotoLink(@QueryMap Map<String,String> filters);
}
