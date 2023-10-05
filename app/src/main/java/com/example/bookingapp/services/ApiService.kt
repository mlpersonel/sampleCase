package com.example.bookingapp.services

import com.example.bookingapp.models.Station
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// Retrofit servisi
interface ApiService {
    @GET("case-study/6/stations/")
    fun getStations(): Call<List<Station>>

    @POST("case-study/6/stations/{stationId}/trips/{tripId}")
    fun tripReserve(
        @Path("stationId") stationId: Int,
        @Path("tripId") tripId: Int
    ): Call<ResponseBody>

}