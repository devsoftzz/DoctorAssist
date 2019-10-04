package com.devsoftzz.doctorassist.ApiHelpers;

import com.devsoftzz.doctorassist.Models.Place;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {


    //https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=23.1881176,72.627997&radius=5000&type=hospital&keyword=heart&key=AIzaSyCHnirJxrTjyh0JYG9HZOe5RazRhtjYFl0


    @GET("place/nearbysearch/json")
    Call<Place> getPlaces(@Query("location") String coordinates, @Query("radius") String radius, @Query("type") String type, @Query("keyword") String keyword, @Query("key") String apikey);

}

