package com.example.maphomework.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.maphomework.databinding.FragmentCustomMapBinding
import com.google.android.gms.location.LocationListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class CustomMapFragment : Fragment() {

    private var _binding: FragmentCustomMapBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val locationService by inject<LocationService>()

    private var googleMap: GoogleMap? = null
    private var locationListener: LocationSource.OnLocationChangedListener? = null

    @SuppressLint("MissingPermission")
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permissionGranted ->
        if (permissionGranted) {

//            subscribeLocationUpdates()

//            viewLifecycleOwner.lifecycleScope.launch {
//                val location = locationService.getCurrentLocation() ?: return@launch
//                Toast.makeText(
//                    requireContext(),
//                    "Location: ${location.latitude}, ${location.longitude}", Toast.LENGTH_SHORT
//                ).show()
//            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentCustomMapBinding.inflate(inflater, container, false)
            .also { _binding = it }
            .root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        locationService.getLocationFlow().onEach {
            if (it != null) {
                locationListener?.onLocationChanged(it)
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        with(binding) {
            mapView.getMapAsync { map ->
                googleMap = map.apply {
                    uiSettings.isCompassEnabled = true
                    uiSettings.isZoomControlsEnabled = true
                    uiSettings.isMyLocationButtonEnabled = true
                    isMyLocationEnabled = hasLocationPermission()
                }
                map.setLocationSource(object : LocationSource {
                    override fun activate(listener: LocationSource.OnLocationChangedListener) {
                        locationListener = listener
                    }

                    override fun deactivate() {
                        locationListener = null
                    }

                })
                map.addMarker(
                    MarkerOptions()
                        .title("Marker 1")
                        .position(
                            LatLng(53.0909700, 27.518913)
                        )
                )
                map.addMarker(
                    MarkerOptions()
                        .title("Marker 2")
                        .position(
                            LatLng(10.0, 10.0)
                        )
                )
                map.addMarker(
                    MarkerOptions()
                        .title("Marker 3")
                        .position(
                            LatLng(44.9, 19.0)
                        )
                )

            }
            mapView.onCreate(savedInstanceState)
//            ViewCompat.setOnApplyWindowInsetsListener(mapView) { view, insets ->
//                WindowInsetsCompat.CONSUMED
//            }

        }


    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

//    @SuppressLint("MissingPermission")
//    private fun subscribeLocationUpdates() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            locationService
//                .getLocationFlow()
//                .onEach { location ->
//                    delay(3000)
//                    binding.textView.text = "Location: ${location?.latitude}, ${location?.longitude}"
//                }
//                .launchIn(viewLifecycleOwner.lifecycleScope)
//        }
//    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun moveCameraToLocation(location: Location) {
        val current = LatLng(location.latitude, location.longitude)
        googleMap?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(current, MAP_ZOOM)
        )
    }

    companion object {
        private const val MAP_ZOOM = 17f
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onDestroy()
        googleMap = null
        _binding = null
    }

}