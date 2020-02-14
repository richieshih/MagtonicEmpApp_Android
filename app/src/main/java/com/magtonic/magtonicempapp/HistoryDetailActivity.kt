package com.magtonic.magtonicempapp

import android.Manifest
import android.content.Context

import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle

//import android.support.v4.content.ContextCompat
import androidx.core.content.ContextCompat
//import android.support.v7.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.magtonic.magtonicempapp.MainActivity.Companion.historyList

import com.magtonic.magtonicempapp.persistence.History
import java.text.SimpleDateFormat
import java.util.*

class HistoryDetailActivity: AppCompatActivity() , OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    LocationListener {
    private val mTag = HistoryDetailActivity::class.java.name

    private var mContext: Context? = null

    private var mGoogleMap: GoogleMap? = null
    private var mGoogleApiClient: GoogleApiClient? = null

    private var mLocationManager: LocationManager? = null

    private var mLocationRequest: LocationRequest? = null

    //private var mLastLocation: Location? = null
    private var mCurrLocationMarker: Marker? = null

    private var longitude = 0.0
    private var latitude = 0.0
    /*private var desc = ""
    private var date = ""
    private var time = ""
    */
    //private var date: Date? = null

    private var history: History? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_detail)

        val actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.setDisplayUseLogoEnabled(true)
            actionBar.setDisplayShowHomeEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_36)
        }

        val index = intent.getStringExtra("INDEX")

        if (index != null) {
            if (historyList != null) {
                if (historyList!!.size > 0) {
                    history = historyList!![index.toInt()]

                    val cal = Calendar.getInstance(Locale.getDefault())
                    val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                    cal.time = dateFormat.parse(history!!.getDate() as String) as Date
                    //val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)


                    Log.e(mTag, "longitudeString = "+history!!.getLongitude()+

                            " latitudeString = "+history!!.getLatitude()+
                            " desc = "+history!!.getDesc()+
                            " date = "+history!!.getDate()+
                            " time = "+history!!.getTime()+
                            " day of week = "+cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
                    )

                    var dateString = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()) as String
                    dateString += history!!.getTime()+" "
                    dateString += history!!.getDate()

                    Log.e(mTag, "dateString = $dateString")

                    title = dateString
                }
            } else {
                title = getString(R.string.history_detail_title)
            }
        }



        mContext = applicationContext

        //init map
        mLocationManager = mContext!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val mapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.mapHistoryDetail) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    override fun onDestroy() {
        Log.i(mTag, "onDestroy")

        if (mGoogleApiClient != null) {
            //val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext as Context)
            //mFusedLocationClient.removeLocationUpdates(mLocationCallback)

            mGoogleApiClient!!.disconnect()

        }
        mGoogleApiClient = null

        super.onDestroy()
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        //menuInflater.inflate(R.menu.detail_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }

        }

        return true
    }

    @Synchronized
    private fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(mContext as Context)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        mGoogleApiClient!!.connect()
    }

    override fun onConnected(p0: Bundle?) {
        Log.e(mTag, "onConnected")
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 1000
        mLocationRequest!!.fastestInterval = 1000
        mLocationRequest!!.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

        val accessFineLocationPermission = ContextCompat.checkSelfPermission(mContext as Context, Manifest.permission.ACCESS_FINE_LOCATION)

        if (accessFineLocationPermission == PackageManager.PERMISSION_GRANTED) {

            //val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext as Context)
            //mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
            if (mCurrLocationMarker != null) {
                mCurrLocationMarker!!.remove()
            }

            val latLng = LatLng(history!!.getLatitude() as Double, history!!.getLongitude() as Double)
            val markerOptions = MarkerOptions()
            markerOptions.position(latLng)
            markerOptions.title(history!!.getDesc()+" "+history!!.getDate()+" "+history!!.getTime())
            markerOptions.snippet(getString(R.string.map_snippet, latLng.longitude, latLng.latitude))


            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
            mCurrLocationMarker = mGoogleMap!!.addMarker(markerOptions)

            //move map camera
            mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
        }
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.e(mTag, "onConnectionSuspended")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.e(mTag, "onConnectionFailed")
    }

    //, com.google.android.gms.location.LocationListener
    override fun onLocationChanged(location: Location?) {

        Log.e(mTag, "onLocationChanged")

        /*mLastLocation = location
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker!!.remove()
        }

        try {
            //Place current location marker
            val latLng = LatLng(location!!.latitude, location.longitude)
            val markerOptions = MarkerOptions()
            markerOptions.position(latLng)
            markerOptions.title(getString(R.string.map_marker_my_locate))
            markerOptions.snippet(getString(R.string.map_snippet, location.longitude, location.latitude))


            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
            mCurrLocationMarker = mGoogleMap!!.addMarker(markerOptions)

            //move map camera
            mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
        } catch (e: Exception) {
            e.printStackTrace()
        }



        //optionally, stop location updates if only current location is needed
        if (mGoogleApiClient != null) {
            //, com.google.android.gms.location.LocationListener
            val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext as Context)
            mFusedLocationClient.removeLocationUpdates(mLocationCallback)
            //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
        }*/

    }

    override fun onMapReady(googleMap: GoogleMap?) {


        Log.d(mTag, "onMapReady")

        mGoogleMap = googleMap

        //Initialize Google Play Services
        buildGoogleApiClient()
        try {
            //mGoogleMap!!.setMyLocationEnabled(true)

            mGoogleMap!!.isMyLocationEnabled = true
            val myLocation = getLastKnownLocation()

            if (myLocation != null) {
                longitude = myLocation.longitude
                latitude = myLocation.latitude
                Log.d(mTag, "longitude = $longitude latitude = $latitude")
            } else {
                Log.d(mTag, "location = null")
            }




            /*googleMap!!.setOnMapClickListener {
                //toast(getString(R.string.map_snippet, it.longitude, it.latitude))
                Log.e(mTag, "Latitude: "+ it.longitude+", Longitude: " + it.latitude)

                currentMarkerNum++

                val markerOptions = MarkerOptions()
                markerOptions.position(it)
                markerOptions.title(getString(R.string.map_maker_add, currentMarkerNum))
                markerOptions.snippet(getString(R.string.map_snippet, it.longitude, it.latitude))

                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                mCurrLocationMarker = mGoogleMap!!.addMarker(markerOptions)

                if (!isFabShowing) {
                    val showIntent = Intent()
                    showIntent.action = Constants.ACTION.ACTION_MAP_SHOW_CLEAR
                    mContext!!.sendBroadcast(showIntent)

                    isFabShowing = true
                }

                // Creating a marker
                /*var markerOptions: MarkerOptions? = null

                // Setting the position for the marker
                markerOptions!!.position(it)

                // Setting the title for the marker.
                // This will be displayed on taping the marker
                markerOptions.title("Latitude: "+ it.latitude+", Longitude" + it.longitude)

                // Clears the previously touched position
                googleMap.clear()

                // Animating to the touched position
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))

                // Placing a marker on the touched position
                googleMap.addMarker(markerOptions)*/

            }*/

            /*googleMap.setOnInfoWindowClickListener {

                if (it!= null) {

                    //if (!it.title.equals(getString(R.string.map_marker_my_locate))) {
                    if (it.title != getString(R.string.map_marker_my_locate)) {
                        if (currentMarkerNum > 0) {
                            currentMarkerNum--
                        }
                        it.remove()

                        if (currentMarkerNum == 0) { //hide fab button

                            if (isFabShowing) {
                                val hideIntent = Intent()
                                hideIntent.action = Constants.ACTION.ACTION_MAP_HIDE_CLEAR
                                mContext!!.sendBroadcast(hideIntent)

                                isFabShowing = false
                            }
                        }
                    }


                }
            }*/
            //LatLng sydney = new LatLng(22.631392, 120.301803);
            //mGoogleMap.addMarker(new MarkerOptions().position(sydney).title("捷運美麗島站"));

            /*mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {

                    Log.d(mTag, "onMyLocationButtonClick!");

                    Location myLocation = getLastKnownLocation();

                    if (myLocation != null) {
                        double longitude = myLocation.getLongitude();
                        double latitude = myLocation.getLatitude();
                        Log.d(mTag, "longitude = "+longitude+" latitude = "+latitude);
                    } else {
                        Log.d(mTag, "location = null");
                    }


                    return false;
                }
            });*/
        } catch (e: SecurityException) {
            e.printStackTrace()
        }

    }

    private fun getLastKnownLocation(): Location? {

        val providers = mLocationManager!!.getProviders(true)
        var bestLocation: Location? = null
        for (provider in providers) {
            var l: Location? = null
            try {
                l = mLocationManager!!.getLastKnownLocation(provider)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }

            if (l == null) {
                continue
            }
            if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                // Found best last known location: %s", l);
                bestLocation = l
            }
        }
        return bestLocation
    }




    /*private val mLocationCallback =  object : LocationCallback() {

        override fun onLocationResult(locationResult : LocationResult) {
            val locationList = locationResult.locations
            if (locationList.size > 0) {
                //The last location in the list is the newest
                val location = locationList[locationList.size - 1]
                Log.i("onLocationResult", "Location: " + location.latitude + " " + location.longitude)

                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker!!.remove()
                }

                val latLng = LatLng(history!!.getLatitude() as Double, history!!.getLongitude() as Double)
                val markerOptions = MarkerOptions()
                markerOptions.position(latLng)
                markerOptions.title(history!!.getDesc()+" "+history!!.getDate()+" "+history!!.getTime())
                markerOptions.snippet(getString(R.string.map_snippet, latLng.longitude, latLng.latitude))


                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                mCurrLocationMarker = mGoogleMap!!.addMarker(markerOptions)

                //move map camera
                mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
            }
        }
    }*/


}