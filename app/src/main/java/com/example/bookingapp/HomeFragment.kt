package com.example.bookingapp

import android.content.Context
import com.example.bookingapp.models.Station
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.bookingapp.databinding.FragmentHomeBinding
import com.example.bookingapp.services.ApiClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentHomeBinding? = null

    private lateinit var mMap: GoogleMap
    private lateinit var bottomSheet: LinearLayout
    private lateinit var markerButton: Button
    private var visibleMarker: Marker? = null

    private val binding get() = _binding!!

    private val args: HomeFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (args.station != null) {
            visibleMarker?.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.completed))
        }

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        bottomSheet = binding.bottomSheet
        markerButton = binding.markerButton

        fetchAllStationsRequest()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchAllStationsRequest() {

        val apiService =
            ApiClient.create()
        val call = apiService.getStations()

        call.enqueue(object : Callback<List<Station>> {
            override fun onResponse(call: Call<List<Station>>, response: Response<List<Station>>) {
                if (response.isSuccessful) {
                    val stations = response.body()
                    stations?.let { stationsOnMap(it) }
                } else {

                    println("MapsActivity Error Response code: ${response.code()}")
                    handleFailure(response.message() ?: getString(R.string.unknown_error))
                }
            }

            override fun onFailure(call: Call<List<Station>>, t: Throwable) {
                println(
                    getString(
                        R.string.mapsactivity_error_onFailure,
                        t.localizedMessage ?: getString(R.string.unknown_error)
                    )
                )
                handleFailure(t.localizedMessage ?: getString(R.string.unknown_error))
            }
        })
    }

    internal fun handleFailure(errorMessage: String) {

        Toast.makeText(requireContext(), getString(R.string.error, errorMessage), Toast.LENGTH_LONG).show()

        println(getString(R.string.mapsactivity_error, errorMessage))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = false

        mMap.setOnMapClickListener {
            hideBottomSheet()
        }

        mMap.setOnMarkerClickListener { clickedMarker ->
            if (clickedMarker != visibleMarker) {
                visibleMarker?.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.point))
                clickedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.selected_point))
                visibleMarker = clickedMarker
                showBottomSheet(clickedMarker)
                if (clickedMarker.isInfoWindowShown) {
                    clickedMarker.hideInfoWindow()
                } else {
                    clickedMarker.showInfoWindow()
                }
            }
            true
        }
    }

    private fun addStationMarker(station: Station) {
        val markerIcon = BitmapFactory.decodeResource(resources, R.drawable.point)
        val customMarker = BitmapDescriptorFactory.fromBitmap(markerIcon)

        val coordinates = station.center_coordinates.split(",")
        val latitude = coordinates[0].toDouble()
        val longitude = coordinates[1].toDouble()

        val cameraPosition = CameraPosition.Builder()
            .target(LatLng(41.09297645004368, 29.003123581510543))
            .zoom(12f)
            .build()

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

        val marker = mMap
            .addMarker(
                MarkerOptions()
                    .position(LatLng(latitude, longitude))
                    .title(station.name)
                    .snippet("${station.trips_count} Trips")
                    .icon(customMarker)
            )
        if (marker != null) {
            marker.tag = station
        }
    }

    private fun showBottomSheet(marker: Marker) {
        if (visibleMarker != null && visibleMarker != marker) {
            hideBottomSheet()
        }
        markerButton.setOnClickListener {
            val selectedStation = marker.tag as Station
            val action = HomeFragmentDirections.actionFirstFragmentToSecondFragment(selectedStation)
            findNavController().navigate(action)
        }
        bottomSheet.visibility = View.VISIBLE
        visibleMarker = marker
    }

    private fun hideBottomSheet() {
        bottomSheet.visibility = View.GONE
        visibleMarker = null
    }

    internal fun stationsOnMap(stations: List<Station>) {
        if (stations.isNotEmpty()) {
            stations.forEach { station ->
                if (station.center_coordinates.isNotEmpty()) {
                    addStationMarker(station)
                }
            }
        }
    }
}