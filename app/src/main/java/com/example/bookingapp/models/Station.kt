package com.example.bookingapp.models

import com.google.gson.Gson
import java.io.Serializable

data class Station(
    val center_coordinates: String,
    val id: Int,
    val name: String,
    val trips: List<Trip>?,
    val trips_count: Int
) : Serializable


fun Station.toJson(): String {
    return Gson().toJson(this)
}