package com.example.bookingapp

import StationAdapter
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookingapp.databinding.FragmentTripsBinding
import com.example.bookingapp.services.ApiClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class TripsFragment : Fragment() {

    private lateinit var toolbar: Toolbar

    private var _binding: FragmentTripsBinding? = null
    private val binding get() = _binding!!

    internal val args: TripsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentTripsBinding.inflate(inflater, container, false)

        val station = args.station

        setupToolbar()

        val recyclerView = binding.recyclerView
        val layoutManager = LinearLayoutManager(requireContext())
        val dividerItemDecoration =
            DividerItemDecoration(recyclerView.context, layoutManager.orientation)

        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(dividerItemDecoration)

        val stationAdapter = StationAdapter(station.trips) { selectedTrip ->
            checkTripRequest(station.id, selectedTrip.id)
        }

        recyclerView.adapter = stationAdapter

        return binding.root
    }

    private fun checkTripRequest(stationId: Int, tripId: Int) {
        val apiService = ApiClient.create()
        val call = apiService.tripReserve(stationId, tripId)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    println(getString(R.string.apiclient_success))
                    val action =
                        TripsFragmentDirections.actionSecondFragmentToFirstFragment()
                    action.station = args.station
                    findNavController().navigate(action)
                } else {
                    ApiClient.handleFailure(response.message() ?: getString(R.string.unknown_error))
                    showDialogError()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                ApiClient.handleFailure(t.localizedMessage ?: getString(R.string.unknown_error))
            }
        })
    }

    private fun setupToolbar() {
        toolbar = binding.customToolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        val upArrow = ContextCompat.getDrawable(requireContext(), android.R.drawable.ic_menu_today)
        upArrow?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black), PorterDuff.Mode.SRC_ATOP)
        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(upArrow)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setHomeButtonEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.title =
            getString(R.string.trips_fragment_toolbar_label)
    }

    internal fun showDialogError() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_alert_dialog,
            null
        )
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()
        val selectTripButton = dialogView.findViewById<Button>(R.id.selectTripButton)
        selectTripButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}