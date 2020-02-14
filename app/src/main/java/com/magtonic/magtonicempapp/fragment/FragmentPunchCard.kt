package com.magtonic.magtonicempapp.fragment

import android.Manifest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager

import android.graphics.Rect

import android.location.Location

import android.location.LocationManager

import android.os.Bundle
import android.os.Looper

//import android.support.v4.app.Fragment
import androidx.fragment.app.Fragment
//import android.support.v4.content.ContextCompat
import androidx.core.content.ContextCompat


import android.util.Log
import android.view.*
import android.widget.*
import com.magtonic.magtonicempapp.MainActivity
import com.magtonic.magtonicempapp.R
import com.magtonic.magtonicempapp.data.Constants
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*

import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.magtonic.magtonicempapp.MainActivity.Companion.db
import com.magtonic.magtonicempapp.MainActivity.Companion.historyList
import com.magtonic.magtonicempapp.MainActivity.Companion.isGPSEnabled
import com.magtonic.magtonicempapp.persistence.History
import java.text.SimpleDateFormat
import java.util.*


class FragmentPunchCard: Fragment() ,OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {



    private val mTag = FragmentPunchCard::class.java.name
    private var punchCardContext: Context? = null

    private var mReceiver: BroadcastReceiver? = null
    private var isRegister = false

    private var progressBar: ProgressBar? = null
    private var relativeLayout: RelativeLayout? = null
    private var linearLayout: LinearLayout? = null

    private var btnClockOn: Button? = null
    private var btnClockOff: Button? = null
    private var btnGoOut: Button? = null
    private var btnGoBack: Button? = null
    private var btnWorkOvertime: Button? = null
    private var btnOffWorkOvertime: Button? = null

    private var mGoogleMap: GoogleMap? = null
    private var mGoogleApiClient: GoogleApiClient? = null

    private var mLocationManager: LocationManager? = null


    //internal var mapFrag: SupportMapFragment? = null
    private var mLocationRequest: LocationRequest? = null

    private var mLastLocation: Location? = null
    internal var mCurrLocationMarker: Marker? = null


    //private var currentLatLng: LatLng? = null
    //internal var mFragment: SupportMapFragment? = null
    //internal var currLocationMarker: Marker? = null
    //internal var locationListener: LocationListener? = null

    //private var searchView: SearchView? = null

    //internal var mMarkers: MutableList<Marker> = ArrayList()


    private var longitude = 0.0
    private var latitude = 0.0

    private var currentMarkerNum = 0
    private var isFabShowing: Boolean = false

    private var mSettingsClient: SettingsClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e(mTag, "onCreate")

        punchCardContext = context

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d(mTag, "onCreateView")

        val view = inflater.inflate(R.layout.fragment_punchcard, container, false)

        btnClockOn = view.findViewById(R.id.frag_mobilepc_function_button_work)
        btnClockOff = view.findViewById(R.id.frag_MobilePc_function_button_OffWork)
        btnGoOut = view.findViewById(R.id.frag_MobilePc_Function_Button_GoOut)
        btnGoBack = view.findViewById(R.id.frag_mobilepc_function_button_btf)
        btnWorkOvertime = view.findViewById(R.id.frag_mobilepc_function_button_work_overtime)
        btnOffWorkOvertime = view.findViewById(R.id.frag_mobilepc_function_button_offwwork_overtime)


        relativeLayout = view.findViewById(R.id.punchCard_container)
        progressBar = ProgressBar(punchCardContext, null, android.R.attr.progressBarStyleLarge)
        val params = RelativeLayout.LayoutParams(MainActivity.screenHeight / 4, MainActivity.screenWidth / 4)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)
        relativeLayout!!.addView(progressBar, params)
        progressBar!!.visibility = View.GONE

        //detect soft keyboard
        linearLayout = view.findViewById(R.id.linearLayoutPunchCard)
        linearLayout!!.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            linearLayout!!.getWindowVisibleDisplayFrame(r)
            val screenHeight = linearLayout!!.rootView.height
            val keypadHeight = screenHeight - r.bottom
            MainActivity.isKeyBoardShow = (keypadHeight > screenHeight * 0.15)
        }

        btnClockOn!!.setOnClickListener {
            Log.e(mTag, "btnClockOn click")

            progressBar!!.visibility = View.VISIBLE
            btnClockOn!!.isEnabled = false
            btnClockOn!!.visibility = View.INVISIBLE

            val punchIntent = Intent()
            punchIntent.action = Constants.ACTION.ACTION_PUNCH_CARD_ACTION
            punchIntent.putExtra("CODE", "00")
            punchIntent.putExtra("LATITUDE", latitude)
            punchIntent.putExtra("LONGITUDE", longitude)
            punchCardContext!!.sendBroadcast(punchIntent)
        }

        btnClockOff!!.setOnClickListener {
            Log.e(mTag, "btnClockOff click")

            progressBar!!.visibility = View.VISIBLE
            btnClockOff!!.isEnabled = false
            btnClockOff!!.visibility = View.INVISIBLE

            val punchIntent = Intent()
            punchIntent.action = Constants.ACTION.ACTION_PUNCH_CARD_ACTION
            punchIntent.putExtra("CODE", "01")
            punchIntent.putExtra("LATITUDE", latitude)
            punchIntent.putExtra("LONGITUDE", longitude)
            punchCardContext!!.sendBroadcast(punchIntent)
        }

        btnGoOut!!.setOnClickListener {
            Log.e(mTag, "btnGoOut click")

            progressBar!!.visibility = View.VISIBLE
            btnGoOut!!.isEnabled = false
            btnGoOut!!.visibility = View.INVISIBLE

            val punchIntent = Intent()
            punchIntent.action = Constants.ACTION.ACTION_PUNCH_CARD_ACTION
            punchIntent.putExtra("CODE", "02")
            punchIntent.putExtra("LATITUDE", latitude)
            punchIntent.putExtra("LONGITUDE", longitude)
            punchCardContext!!.sendBroadcast(punchIntent)
        }

        btnGoBack!!.setOnClickListener {
            Log.e(mTag, "btnGoBack click")

            progressBar!!.visibility = View.VISIBLE
            btnGoBack!!.isEnabled = false
            btnGoBack!!.visibility = View.INVISIBLE

            val punchIntent = Intent()
            punchIntent.action = Constants.ACTION.ACTION_PUNCH_CARD_ACTION
            punchIntent.putExtra("CODE", "03")
            punchIntent.putExtra("LATITUDE", latitude)
            punchIntent.putExtra("LONGITUDE", longitude)
            punchCardContext!!.sendBroadcast(punchIntent)
        }

        btnWorkOvertime!!.setOnClickListener {
            Log.e(mTag, "btnWorkOvertime click")

            progressBar!!.visibility = View.VISIBLE
            btnWorkOvertime!!.isEnabled = false
            btnWorkOvertime!!.visibility = View.INVISIBLE

            val punchIntent = Intent()
            punchIntent.action = Constants.ACTION.ACTION_PUNCH_CARD_ACTION
            punchIntent.putExtra("CODE", "04")
            punchIntent.putExtra("LATITUDE", latitude)
            punchIntent.putExtra("LONGITUDE", longitude)
            punchCardContext!!.sendBroadcast(punchIntent)
        }

        btnOffWorkOvertime!!.setOnClickListener {
            Log.e(mTag, "btnOffWorkOvertime click")

            progressBar!!.visibility = View.VISIBLE
            btnOffWorkOvertime!!.isEnabled = false
            btnOffWorkOvertime!!.visibility = View.INVISIBLE

            val punchIntent = Intent()
            punchIntent.action = Constants.ACTION.ACTION_PUNCH_CARD_ACTION
            punchIntent.putExtra("CODE", "05")
            punchIntent.putExtra("LATITUDE", latitude)
            punchIntent.putExtra("LONGITUDE", longitude)
            punchCardContext!!.sendBroadcast(punchIntent)
        }

        /*var textViewTitle : TextView = view.findViewById(R.id.first_load_textView_title2)
        fragStaffinfoWifiId = view.findViewById(R.id.frag_staffinfo_wifi_id)
        fragStaffinfoWifiStatus = view.findViewById(R.id.frag_staffinfo_wifi_status)
        acFirstTimeUseStaffNum = view.findViewById(R.id.ac_first_time_use_staff_num)
        acFirstTimeUsePassword = view.findViewById(R.id.ac_first_time_use_password)
        acFirstTimeUseLoginButton = view.findViewById(R.id.ac_first_time_use_login_button)

        val title1 = getString(R.string.first_company_name)
        val title2 = getString(R.string.loadingTitle2)
        val logintitle = title1 + title2
        textViewTitle.text = logintitle

        mWifiManager = loginContext!!.getSystemService(Context.WIFI_SERVICE) as WifiManager

        if (!mWifiManager!!.isWifiEnabled) {
            mWifiManager!!.isWifiEnabled = true
        }

        getDeviceImei()

        mWifiScannThread = WifiScannThread()
        mWifiScannThread!!.start()
        mWifiTools = WifiTools(mWifiManager as WifiManager)

        acFirstTimeUseLoginButton!!.setOnClickListener {
            progressBar!!.visibility = View.VISIBLE
            acFirstTimeUseLoginButton!!.visibility= View.INVISIBLE

            val account: EditText? = acFirstTimeUseStaffNum
            val password: EditText? = acFirstTimeUsePassword
            if (account != null && password != null) {
                if (account.text.isEmpty()) {
                    progressBar!!.visibility = View.GONE
                    acFirstTimeUseLoginButton!!.visibility= View.VISIBLE
                    toast(resources.getString(R.string.login_account_empty))
                } else if (password.text.isEmpty()) {
                    progressBar!!.visibility = View.GONE
                    acFirstTimeUseLoginButton!!.visibility= View.VISIBLE
                    toast(resources.getString(R.string.login_password_empty))
                } else {
                    Log.d(mTag, "no other ")
                    val loginIntent = Intent()
                    loginIntent.action = Constants.ACTION.ACTION_LOGIN_ACTION
                    loginIntent.putExtra("account", account.text.toString())
                    loginIntent.putExtra("password", password.text.toString())
                    loginIntent.putExtra("imei", IMEI)
                    loginContext!!.sendBroadcast(loginIntent)



                }
            }
        }*/

        //init map
        mLocationManager = punchCardContext!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        mSettingsClient = LocationServices.getSettingsClient(punchCardContext as Context)

        if (mLocationManager != null) {
            isGPSEnabled = mLocationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)

            Log.d(mTag, "isGPSEnabled = $isGPSEnabled")

            if (!isGPSEnabled) {
                if (isAdded) {
                    toast(getString(R.string.punchcard_gps_disable))
                }
            } else {
                btnClockOn!!.visibility = View.VISIBLE
                btnClockOff!!.visibility = View.VISIBLE
                btnGoOut!!.visibility = View.VISIBLE
                btnGoBack!!.visibility = View.VISIBLE
                btnWorkOvertime!!.visibility = View.VISIBLE
                btnOffWorkOvertime!!.visibility = View.VISIBLE
            }
        }

        val mapFragment: SupportMapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        //val mapFragment: MapFragment = fragmentManager.findFragmentById(R.id.map) as SupportMapFragment





        val filter: IntentFilter

        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action != null) {
                    when (intent.action) {
                        Constants.ACTION.ACTION_NETWORK_FAILED -> {
                            Log.d(mTag, "ACTION_NETWORK_FAILED")

                            progressBar!!.visibility = View.GONE
                        }
                        Constants.ACTION.ACTION_MAP_MARKER_CLEAR -> {
                            Log.d(mTag, "ACTION_MAP_MARKER_CLEAR")

                            if (mGoogleMap != null) {
                                mGoogleMap!!.clear()


                            }

                            currentMarkerNum = 0
                            isFabShowing = false

                            if (isAdded) {

                                //set last position as my location marker
                                if (mLastLocation != null) {
                                    //Place current location marker
                                    val latLng = LatLng(mLastLocation!!.latitude, mLastLocation!!.longitude)
                                    val markerOptions =  MarkerOptions()
                                    markerOptions.position(latLng)
                                    markerOptions.title(getString(R.string.map_marker_my_locate))
                                    markerOptions.snippet(getString(R.string.map_snippet, mLastLocation!!.longitude, mLastLocation!!.latitude))


                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                                    mCurrLocationMarker = mGoogleMap!!.addMarker(markerOptions)

                                    //move map camera
                                    mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))


                                } else {
                                    Log.d(mTag, "mLastLocation = null")
                                    toast(getString(R.string.punchcard_gps_locate_failed))
                                    //val latLng = LatLng(myLocation!!.getLatitude(), myLocation.getLongitude())

                                }

                                /*try {
                                    val myLocation = getLastKnownLocation()

                                    if (myLocation != null) {
                                        longitude = myLocation.longitude
                                        latitude = myLocation.latitude
                                        Log.d(mTag, "longitude = $longitude latitude = $latitude")

                                        val latLng = LatLng(myLocation!!.latitude, myLocation.longitude)
                                        val markerOptions = MarkerOptions()
                                        markerOptions.position(latLng)
                                        markerOptions.title(getString(R.string.map_marker_my_locate))
                                        markerOptions.snippet(getString(R.string.map_snippet, myLocation.longitude, myLocation.latitude))

                                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                                        mCurrLocationMarker = mGoogleMap!!.addMarker(markerOptions)

                                        //move map camera
                                        mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
                                    } else {
                                        Log.d(mTag, "location = null")
                                        toast(getString(R.string.punchcard_gps_locate_failed))
                                    }

                                    //val latLng = LatLng(myLocation!!.getLatitude(), myLocation.getLongitude())

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }*/
                            } else {
                                Log.e(mTag, "Fragment is not added")
                            }
                        }
                        Constants.ACTION.ACTION_PUNCH_CARD_FAILED -> {
                            Log.d(mTag, "ACTION_PUNCH_CARD_FAILED")

                            progressBar!!.visibility = View.GONE
                        }
                        Constants.ACTION.ACTION_PUNCH_CARD_SUCCESS -> {
                            Log.d(mTag, "ACTION_PUNCH_CARD_SUCCESS")

                            val code = intent.getStringExtra("CODE")

                            val hours = intent.getStringExtra("TIME_HOURS")
                            val minutes = intent.getStringExtra("TIME_MINUTES")

                            Log.e(mTag, "code = $code hours = $hours minutes = $minutes")

                            val history: History
                            val c  = Calendar.getInstance(Locale.getDefault())
                            val dateOnly = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                            val dateString = dateOnly.format(c.time)
                            if (code != null && code.isNotEmpty()) {
                                if (isAdded) {
                                    when (code) {
                                        "00" -> {
                                            history = History(code, getString(R.string.punchcard_clock_on_success), dateString, "$hours:$minutes", latitude, longitude)
                                            toast(getString(R.string.punchcard_clock_on_success))
                                        }
                                        "01" -> {
                                            history = History(code, getString(R.string.punchcard_clock_off_success), dateString, "$hours:$minutes", latitude, longitude)
                                            toast(getString(R.string.punchcard_clock_off_success))
                                        }
                                        "02" -> {
                                            history = History(code, getString(R.string.punchcard_go_out_success), dateString, "$hours:$minutes", latitude, longitude)
                                            toast(getString(R.string.punchcard_go_out_success))
                                        }
                                        "03" -> {
                                            history = History(code, getString(R.string.punchcard_go_back_success), dateString, "$hours:$minutes", latitude, longitude)
                                            toast(getString(R.string.punchcard_go_back_success))
                                        }
                                        "04" -> {
                                            history = History(code, getString(R.string.punchcard_go_back_success), dateString, "$hours:$minutes", latitude, longitude)
                                            toast(getString(R.string.punchcard_work_overtime_success))
                                        }
                                        "05" -> {
                                            history = History(code, getString(R.string.punchcard_offwork_overtime_success), dateString, "$hours:$minutes", latitude, longitude)
                                            toast(getString(R.string.punchcard_offwork_overtime_success))
                                        }
                                        else -> {

                                            val timeOnly = SimpleDateFormat("hh:mm", Locale.getDefault())

                                            val timeString = timeOnly.format(c.time)
                                            history = History("Unknown code", "Unknown code", dateString, timeString, 0.0, 0.0)
                                            toast("Unknown code")
                                        }
                                    }

                                    historyList!!.add(0, history)
                                    if (db != null) {
                                        db!!.historyDao().insert(history)
                                    } else {
                                        Log.e(mTag, "db = null")
                                    }
                                }
                            } else {
                                Log.e(mTag, "code = null")
                            }

                            progressBar!!.visibility = View.GONE
                            btnClockOn!!.isEnabled = true
                            btnClockOff!!.isEnabled = true
                            btnGoOut!!.isEnabled = true
                            btnGoBack!!.isEnabled = true
                            btnWorkOvertime!!.isEnabled = true
                            btnOffWorkOvertime!!.isEnabled = true

                            btnClockOn!!.visibility = View.VISIBLE
                            btnClockOff!!.visibility = View.VISIBLE
                            btnGoOut!!.visibility = View.VISIBLE
                            btnGoBack!!.visibility = View.VISIBLE
                            btnWorkOvertime!!.visibility = View.VISIBLE
                            btnOffWorkOvertime!!.visibility = View.VISIBLE
                        }
                    }
                }

                if (intent.action == "android.location.PROVIDERS_CHANGED") {
                    Log.d(mTag, "android.location.PROVIDERS_CHANGED")

                    if (mLocationManager != null) {
                        isGPSEnabled = mLocationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)

                        Log.d(mTag, "isGPSEnabled = $isGPSEnabled")

                        if (!isGPSEnabled) { //GPS disable

                            //hide fab button
                            val hideIntent = Intent()
                            hideIntent.action = Constants.ACTION.ACTION_MAP_HIDE_CLEAR
                            punchCardContext!!.sendBroadcast(hideIntent)
                            isFabShowing = false

                            if (mGoogleMap != null) {
                                mGoogleMap!!.clear()
                            }
                            currentMarkerNum = 0

                            if (isAdded) {
                                if (mLastLocation != null) {
                                    //Place current location marker
                                    val latLng = LatLng(mLastLocation!!.latitude, mLastLocation!!.longitude)
                                    val markerOptions =  MarkerOptions()
                                    markerOptions.position(latLng)
                                    markerOptions.title(getString(R.string.map_marker_my_locate))
                                    markerOptions.snippet(getString(R.string.map_snippet, mLastLocation!!.longitude, mLastLocation!!.latitude))


                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                                    mCurrLocationMarker = mGoogleMap!!.addMarker(markerOptions)

                                    //move map camera
                                    mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))


                                } else {
                                    Log.d(mTag, "mLastLocation = null")

                                    //val latLng = LatLng(myLocation!!.getLatitude(), myLocation.getLongitude())

                                }
                                toast(getString(R.string.punchcard_gps_disable))
                            } else {
                                Log.e(mTag, "Fragment is not added")
                            }

                            //set last position as my location marker


                            btnClockOn!!.visibility = View.GONE
                            btnClockOff!!.visibility = View.GONE
                            btnGoOut!!.visibility = View.GONE
                            btnGoBack!!.visibility = View.GONE
                            btnWorkOvertime!!.visibility = View.GONE
                            btnOffWorkOvertime!!.visibility = View.GONE

                        } else {
                            //hide fab button
                            val hideIntent = Intent()
                            hideIntent.action = Constants.ACTION.ACTION_MAP_HIDE_CLEAR
                            punchCardContext!!.sendBroadcast(hideIntent)
                            isFabShowing = false

                            if (mGoogleMap != null) {
                                mGoogleMap!!.clear()
                            }
                            currentMarkerNum = 0

                            if (isAdded) {
                                if (mLastLocation != null) {
                                    //Place current location marker
                                    val latLng = LatLng(mLastLocation!!.latitude, mLastLocation!!.longitude)
                                    val markerOptions =  MarkerOptions()
                                    markerOptions.position(latLng)
                                    markerOptions.title(getString(R.string.map_marker_my_locate))
                                    markerOptions.snippet(getString(R.string.map_snippet, mLastLocation!!.longitude, mLastLocation!!.latitude))


                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                                    mCurrLocationMarker = mGoogleMap!!.addMarker(markerOptions)

                                    //move map camera
                                    mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))


                                } else {
                                    Log.d(mTag, "mLastLocation = null")

                                    //val latLng = LatLng(myLocation!!.getLatitude(), myLocation.getLongitude())

                                }
                            } else {
                                Log.e(mTag, "Fragment is not added")
                            }

                            btnClockOn!!.visibility = View.VISIBLE
                            btnClockOff!!.visibility = View.VISIBLE
                            btnGoOut!!.visibility = View.VISIBLE
                            btnGoBack!!.visibility = View.VISIBLE
                            btnWorkOvertime!!.visibility = View.VISIBLE
                            btnOffWorkOvertime!!.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }


        if (!isRegister) {
            filter = IntentFilter()
            filter.addAction(Constants.ACTION.ACTION_NETWORK_FAILED)
            filter.addAction(Constants.ACTION.ACTION_MAP_MARKER_CLEAR)
            filter.addAction(Constants.ACTION.ACTION_PUNCH_CARD_FAILED)
            filter.addAction(Constants.ACTION.ACTION_PUNCH_CARD_SUCCESS)
            filter.addAction("android.location.PROVIDERS_CHANGED")
            punchCardContext!!.registerReceiver(mReceiver, filter)
            isRegister = true
            Log.d(mTag, "registerReceiver mReceiver")
        }

        return view
    }

    override fun onDestroyView() {
        Log.i(mTag, "onDestroyView")

        if (mGoogleApiClient != null) {
            //, com.google.android.gms.location.LocationListener
            val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(punchCardContext as Context)
            mFusedLocationClient.removeLocationUpdates(mLocationCallback)
            //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
            mGoogleApiClient!!.disconnect()
        }

        mGoogleApiClient = null

        super.onDestroyView()
    }

    @Synchronized
    private fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(punchCardContext as Context)
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

        val accessFineLocationPermission = ContextCompat.checkSelfPermission(punchCardContext as Context, Manifest.permission.ACCESS_FINE_LOCATION)

        if (accessFineLocationPermission == PackageManager.PERMISSION_GRANTED) {

            //, com.google.android.gms.location.LocationListener
            val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(punchCardContext as Context)
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
            //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
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

        mLastLocation = location
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker!!.remove()
        }

        if (isAdded) {
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
        }



        //optionally, stop location updates if only current location is needed
        if (mGoogleApiClient != null) {
            //, com.google.android.gms.location.LocationListener
            val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(punchCardContext as Context)
            mFusedLocationClient.removeLocationUpdates(mLocationCallback)
            //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
        }

        btnClockOn!!.visibility = View.VISIBLE
        btnClockOff!!.visibility = View.VISIBLE
        btnGoOut!!.visibility = View.VISIBLE
        btnGoBack!!.visibility = View.VISIBLE
        btnWorkOvertime!!.visibility = View.VISIBLE
        btnOffWorkOvertime!!.visibility = View.VISIBLE
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

            googleMap!!.setOnMapClickListener {
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
                    punchCardContext!!.sendBroadcast(showIntent)

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

            }

            googleMap.setOnInfoWindowClickListener {

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
                                punchCardContext!!.sendBroadcast(hideIntent)

                                isFabShowing = false
                            }
                        }
                    }


                }
            }
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




    private val mLocationCallback =  object : LocationCallback() {

        override fun onLocationResult(locationResult :LocationResult) {
            val locationList = locationResult.locations
            if (locationList.size > 0) {
                //The last location in the list is the newest
                val location = locationList[locationList.size - 1]
                Log.i("onLocationResult", "Location: " + location.latitude + " " + location.longitude)

                latitude = location.latitude
                longitude = location.longitude

                if (isAdded) {
                    if (mLastLocation == null) { //first time
                        Log.e(mTag, "mLastLocation = null")
                        mLastLocation = location

                        if (mCurrLocationMarker != null) {
                            mCurrLocationMarker!!.remove()
                        }

                        //Place current location marker
                        val latLng = LatLng(location.latitude, location.longitude)
                        val markerOptions =  MarkerOptions()
                        markerOptions.position(latLng)
                        markerOptions.title(getString(R.string.map_marker_my_locate))
                        markerOptions.snippet(getString(R.string.map_snippet, location.longitude, location.latitude))


                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                        mCurrLocationMarker = mGoogleMap!!.addMarker(markerOptions)

                        //move map camera
                        mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
                    } else { //not first time
                        mLastLocation = location
                    }


                    /*
                    if (mCurrLocationMarker != null) {
                        mCurrLocationMarker!!.remove()
                    }

                    //Place current location marker
                    val latLng = LatLng(location.latitude, location.longitude)
                    var markerOptions =  MarkerOptions()
                    markerOptions.position(latLng)
                    markerOptions.title("Current Position")
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                    mCurrLocationMarker = mGoogleMap!!.addMarker(markerOptions)

                    //move map camera
                    mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))*/
                } else {
                    Log.e(mTag, "Fragment is not added")
                }
            }
        }
    }

    fun toast(message: String) {
        val toast = Toast.makeText(punchCardContext, message, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER, 0, 0)
        val group = toast.view as ViewGroup
        group.setBackgroundResource(R.drawable.toast_corner_round)
        val textView = group.getChildAt(0) as TextView
        textView.textSize = 25.0f
        toast.show()
    }
}