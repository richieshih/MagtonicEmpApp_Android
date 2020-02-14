package com.magtonic.magtonicempapp


import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import androidx.room.Room

import android.content.*



import android.os.Bundle

//import android.support.design.widget.FloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

//import android.support.v4.view.GravityCompat
import androidx.core.view.GravityCompat
//import android.support.v7.app.ActionBarDrawerToggle
import androidx.appcompat.app.ActionBarDrawerToggle
//import android.support.v4.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout
//import android.support.design.widget.NavigationView
import com.google.android.material.navigation.NavigationView

//import android.support.v4.app.Fragment
import androidx.fragment.app.Fragment

//import android.support.v7.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity
//import android.support.v7.widget.Toolbar
import androidx.appcompat.widget.Toolbar
//import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import com.google.gson.Gson

import com.magtonic.magtonicempapp.api.ApiFunc
import com.magtonic.magtonicempapp.data.Constants
import com.magtonic.magtonicempapp.fragment.FragmentHistory
import com.magtonic.magtonicempapp.fragment.FragmentLogin

import com.magtonic.magtonicempapp.fragment.FragmentPunchCard
import com.magtonic.magtonicempapp.model.receive.PCUser
import com.magtonic.magtonicempapp.model.receive.ReceiveTransform
import com.magtonic.magtonicempapp.model.send.HttpPunchPara
import com.magtonic.magtonicempapp.model.send.HttpUserAuthPara
import com.magtonic.magtonicempapp.model.sys.User
import com.magtonic.magtonicempapp.persistence.History
import com.magtonic.magtonicempapp.persistence.HistoryDataBase
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val mTAG = MainActivity::class.java.name

    //private val REQUEST_ID_MULTIPLE_PERMISSIONS = 1
    //for Log
    //var process: Process? = null

    var pref: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    private val fileName = "Preference"

    private var mContext: Context? = null

    private var imm: InputMethodManager? = null
    private var mReceiver: BroadcastReceiver? = null
    private var isRegister = false

    var navView: NavigationView? = null
    var textViewUserName: TextView? = null

    companion object {
        @JvmStatic var screenWidth: Int = 0
        @JvmStatic var screenHeight: Int = 0
        //@JvmStatic var themeId: Int = -1
        @JvmStatic var user: User? = null
        @JvmStatic var imei: String = ""
        @JvmStatic var isGPSEnabled: Boolean = false
        @JvmStatic var isKeyBoardShow: Boolean = false
        //@JvmStatic var mapFragment: SupportMapFragment? = null
        var db: HistoryDataBase? = null
        @JvmStatic var historyList: ArrayList<History>? = null
    }

    enum class CurrentFragment {
        PUNCHCARD_FRAGMENT, HISTORY_FRAGMENT, LOGIN_FRAGMENT
    }
    var currentFrag: CurrentFragment = CurrentFragment.PUNCHCARD_FRAGMENT

    //private var mTelephonyManager: TelephonyManager? = null

    var account: String = ""
    var password: String = ""
    var username: String = ""

    //for punch
    //var isClockOn: Boolean = false
    //var isGoOut: Boolean = false
    //var isWorkOvertime : Boolean = false


    var fabClear: FloatingActionButton? = null
    var keyboard: MenuItem? = null
    var locate: MenuItem? = null
    var clearHistory: MenuItem? = null

    var currentCode: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(mTAG, "onCreate")

        mContext = applicationContext

        //load db
        db = Room.databaseBuilder(mContext as Context, HistoryDataBase::class.java, HistoryDataBase.DATABASE_NAME)
            .allowMainThreadQueries()
            .build()
        /*try {
                //val packageManager: PackageManager = getPackageManager()
                val packageManager: PackageManager = packageManager
                val activityInfo: ActivityInfo = packageManager.getActivityInfo(getComponentName(), PackageManager.GET_META_DATA)

                themeId = activityInfo.theme
        } catch(e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                Log.e(mTAG, "Could not get themeResId for activity", e)
                themeId = -1
        }*/

        //getDeviceImei()
        //imei = "358885096306040"

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        screenHeight = displayMetrics.heightPixels
        screenWidth = displayMetrics.widthPixels

        Log.e(mTAG, "width = $screenWidth, height = $screenHeight")

        //read user info


        pref = getSharedPreferences(fileName, Context.MODE_PRIVATE)
        account = pref!!.getString(User.USER_ACCOUNT, "") as String
        password = pref!!.getString(User.PASSWORD, "") as String
        username = pref!!.getString(User.USER_NAME, "") as String

        //isClockOn = pref!!.getBoolean("IS_CLOCK_ON", false)
        //isGoOut = pref!!.getBoolean("IS_GO_OUT", false)
        //isWorkOvertime = pref!!.getBoolean("IS_WORK_OVERTIME", false)

        user = User()


        user!!.userAccount = account
        user!!.password = password
        user!!.userName = username

        Log.e(mTAG, "account = "+user!!.userAccount+", password = "+user!!.password+", username = "+user!!.userName)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

        /*val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }*/
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val header = navView!!.inflateHeaderView(R.layout.nav_header_main)
        textViewUserName = header.findViewById(R.id.textViewUserName)
        navView!!.removeHeaderView(navView!!.getHeaderView(0))
        /*val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )*/

        val mDrawerToggle = object : ActionBarDrawerToggle(
            this, /* host Activity */
            drawerLayout, /* DrawerLayout object */
            toolbar, /* nav drawer icon to replace 'Up' caret */
            R.string.navigation_drawer_open, /* "open drawer" description */
            R.string.navigation_drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state.  */

            override fun onDrawerClosed(view: View) {
                super.onDrawerClosed(view)

                Log.d(mTAG, "onDrawerClosed")

            }

            /** Called when a drawer has settled in a completely open state.  */
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)

                Log.d(mTAG, "onDrawerOpened")

                if (isKeyBoardShow) {
                    imm?.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0)
                }
            }
        }

        drawerLayout.addDrawerListener(mDrawerToggle)
        mDrawerToggle.syncState()

        navView!!.setNavigationItemSelectedListener(this)

        //permission



        //check login
        var fragment: Fragment? = null
        //var fragmentClass: Class<*>? = null
        val fragmentClass: Class<*>

        //if (account.equals("") && password.equals("")) {
        if (account.isEmpty() && password.isEmpty()) {
            //set title
            title = getString(R.string.nav_login)

            //show login

            fragmentClass = FragmentLogin::class.java
            currentFrag = CurrentFragment.LOGIN_FRAGMENT

        } else {
            //show receipt
            //set username
            if (textViewUserName != null) {
                //textViewUserName!!.setText(getString(R.string.nav_greeting, username))
                textViewUserName!!.text = getString(R.string.nav_greeting, username)
            } else {
                Log.e(mTAG, "textViewUserName == null")
            }

            //set title
            title = getString(R.string.nav_punchcard)

            //show menu
            navView!!.menu.getItem(0).isVisible = true //punch
            navView!!.menu.getItem(1).isVisible = true //history
            navView!!.menu.getItem(2).isVisible = true //logout
            navView!!.menu.getItem(3).isVisible = false //login
            navView!!.menu.getItem(4).isVisible = true //about

            fragmentClass = FragmentPunchCard::class.java

            currentFrag = CurrentFragment.PUNCHCARD_FRAGMENT
        }

        try {
            fragment = fragmentClass.newInstance() as Fragment
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val fragmentManager = supportFragmentManager
        //fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment!!).commitAllowingStateLoss()

        fabClear = findViewById(R.id.fabClear)

        fabClear!!.setOnClickListener {

            Log.e(mTAG, "fabClear click")
            val clearIntent = Intent()
            clearIntent.action = Constants.ACTION.ACTION_MAP_MARKER_CLEAR
            sendBroadcast(clearIntent)

            //fabClear!!.visibility = View.GONE
            fabClear!!.hide()
        }

        //load from db
        if (db != null) {
            if (historyList != null) {
                historyList!!.clear()
            } else {
                historyList = ArrayList()
            }

            /*val tempList = db!!.historyDao().getAll() as ArrayList<History>

            for (i in tempList.size - 1  downTo 0) {
                historyList!!.add(tempList[i])
            }*/

            historyList = db!!.historyDao().getAll() as ArrayList<History>

            if (historyList!!.size > 1 ) {
                historyList = historyList!!.sortedBy { it.getId() }.reversed() as ArrayList<History>
            }



            Log.e(mTAG, "historyList.size = "+historyList!!.size)

            for (i in 0 until historyList!!.size) {
                Log.e(mTAG, "historyList[$i] = "+ historyList!![i].getId() +
                        " code = "+ historyList!![i].getCode()+
                        " desc = "+ historyList!![i].getDesc()+
                        " date = "+ historyList!![i].getDate()+
                        " time = "+ historyList!![i].getTime()+
                        " Latitude = "+ historyList!![i].getLatitude()+
                        " Longitude = "+ historyList!![i].getLongitude()
                )
            }

        } else {
            Log.e(mTAG, "db = null")
        }

        //filter
        val filter: IntentFilter

        @SuppressLint("CommitPrefEdits")
        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action != null) {

                    when (intent.action) {
                        Constants.ACTION.ACTION_LOGIN_ACTION -> {
                            Log.d(mTAG, "ACTION_LOGIN_ACTION")
                            account = intent.getStringExtra("account") as String
                            password = intent.getStringExtra("password") as String
                            //imei = intent.getStringExtra("imei")

                            Log.e(mTAG, "account = $account password = $password imei = $imei")

                            runOnUiThread {
                                callAPILogin(account, password)
                            }
                        }
                        Constants.ACTION.ACTION_LOGIN_NETWORK_ERROR -> {
                            Log.d(mTAG, "ACTION_LOGIN_NETWORK_ERROR")
                        }
                        Constants.ACTION.ACTION_LOGIN_SERVER_ERROR -> {
                            Log.d(mTAG, "ACTION_LOGIN_SERVER_ERROR")
                        }
                        Constants.ACTION.ACTION_LOGIN_SUCCESS -> {
                            Log.d(mTAG, "ACTION_LOGIN_SUCCESS")

                            title = getString(R.string.nav_punchcard)

                            //set username
                            if (username.isNotEmpty() && username != "") {
                                //textViewUserName!!.setText(getString(R.string.nav_greeting, username))
                                textViewUserName!!.text = getString(R.string.nav_greeting, username)
                            } else {
                                textViewUserName!!.text = account
                            }
                            //save to User
                            user!!.userAccount = account
                            user!!.password = password
                            user!!.userName = username
                            user!!.isLogin = true


                            navView!!.menu.getItem(0).isVisible = true //punch card
                            navView!!.menu.getItem(1).isVisible = true //history
                            navView!!.menu.getItem(2).isVisible = true //logout
                            navView!!.menu.getItem(3).isVisible = false //login
                            navView!!.menu.getItem(4).isVisible = true //about

                            //hide keyboard
                            imm?.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0)

                            //save


                            editor = pref!!.edit()
                            editor!!.putString(User.USER_ACCOUNT, account)
                            editor!!.putString(User.PASSWORD, password)
                            editor!!.putString(User.USER_NAME, username)
                            editor!!.commit()

                            //start with receipt fragment
                            var mFragment: Fragment? = null
                            val mFragmentClass = FragmentPunchCard::class.java

                            try {
                                mFragment = mFragmentClass.newInstance()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }


                            val mFragmentManager = supportFragmentManager
                            //fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                            mFragmentManager.beginTransaction().replace(R.id.flContent, mFragment!!).commitAllowingStateLoss()

                            //set toolbar menu
                            currentFrag = CurrentFragment.PUNCHCARD_FRAGMENT
                            keyboard!!.isVisible = false
                            locate!!.isVisible = true
                            clearHistory!!.isVisible = false
                        }
                        Constants.ACTION.ACTION_LOGIN_FAILED -> {
                            Log.d(mTAG, "ACTION_LOGIN_FAILED")
                        }
                        /*Constants.ACTION.ACTION_SIGNIN_ACTION -> {
                            Log.d(mTAG, "ACTION_SIGNIN_ACTION")

                            account = intent.getStringExtra("account") as String
                            password = intent.getStringExtra("password") as String
                            //imei = intent.getStringExtra("imei")

                            Log.e(mTAG, "account = $account password $password imei = $imei")

                            runOnUiThread {
                                callAPISign(account, imei, password)
                            }
                        }
                        Constants.ACTION.ACTION_SIGNIN_SUCCESS -> {
                            Log.e(mTAG, "account = $account password $password imei = $imei")

                            runOnUiThread {
                                callAPILogin(account, imei)
                            }
                        }*/
                        Constants.ACTION.ACTION_LOGOUT_ACTION -> {
                            Log.d(mTAG, "ACTION_LOGOUT_ACTION")

                            textViewUserName!!.text = ""

                            //save to User
                            user!!.userAccount = ""
                            user!!.password = ""
                            user!!.userName = ""
                            user!!.isLogin = false
                            //save
                            editor = pref?.edit()

                            editor?.putString(User.USER_NAME, "")
                            editor?.putString(User.PASSWORD, "")
                            editor?.putString(User.USER_ACCOUNT, "")
                            editor?.apply()

                            navView!!.menu.getItem(0).isVisible = false //punch card
                            navView!!.menu.getItem(1).isVisible = false //history
                            navView!!.menu.getItem(2).isVisible = false //logout
                            navView!!.menu.getItem(3).isVisible = true //login
                            navView!!.menu.getItem(4).isVisible = true //about

                            var mFragment: Fragment? = null
                            val mFragmentClass = FragmentLogin::class.java

                            try {
                                mFragment = mFragmentClass.newInstance()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }


                            // Insert the fragment by replacing any existing fragment
                            val mFragmentManager = supportFragmentManager
                            //fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                            mFragmentManager.beginTransaction().replace(R.id.flContent, mFragment!!).commitAllowingStateLoss()
                            //set toolbar menu
                            currentFrag = CurrentFragment.LOGIN_FRAGMENT
                            keyboard!!.isVisible = true
                            locate!!.isVisible = false
                            clearHistory!!.isVisible = false
                            title = resources.getString(R.string.nav_login)
                        }
                        Constants.ACTION.ACTION_HIDE_KEYBOARD -> {
                            Log.d(mTAG, "ACTION_HIDE_KEYBOARD")
                            imm?.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0)
                        }
                        Constants.ACTION.ACTION_MAP_HIDE_CLEAR -> {
                            Log.d(mTAG, "ACTION_MAP_HIDE_CLEAR")
                            fabClear!!.visibility = View.GONE
                        }
                        Constants.ACTION.ACTION_MAP_SHOW_CLEAR -> {
                            Log.d(mTAG, "ACTION_MAP_SHOW_CLEAR")
                            fabClear!!.visibility = View.VISIBLE
                        }
                        Constants.ACTION.ACTION_PUNCH_CARD_ACTION -> {
                            Log.d(mTAG, "ACTION_PUNCH_CARD_ACTION")

                            val funCode = intent.getStringExtra("CODE") as String
                            val latitude = intent.getDoubleExtra("LATITUDE", 0.0)
                            val longitude = intent.getDoubleExtra("LONGITUDE", 0.0)

                            val c  = Calendar.getInstance(Locale.getDefault())
                            val dateOnly = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                            val timeOnly = SimpleDateFormat("hh:mm", Locale.getDefault())
                            val dateString = dateOnly.format(c.time)
                            val timeString = timeOnly.format(c.time)


                            //val cal = Calendar.getInstance(Locale.getDefault())
                            //val year = cal.get(Calendar.YEAR)
                            //val month = cal.get(Calendar.MONTH)
                            //val dayofmonth = cal.get(Calendar.DAY_OF_MONTH)

                            Log.d(mTAG, "account = $account ,dateString = $dateString ,timeString $timeString ,funCode $funCode ,imei = $imei ,latitude $latitude ,longitude $longitude")
                            //Log.d(mTAG, "account = $account, dateString = $date_string, time_String = $time_String, funCode = $fun_code, latitude = $latitude, longitude = $longitude")

                            currentCode = funCode

                            runOnUiThread {
                                //callAPIPunch(account, dateString, timeString, imei, funCode, latitude.toString(), longitude.toString())
                                callAPIPunch(account, dateString, timeString, funCode, "mobile", latitude.toString(), longitude.toString())
                            }
                        }
                        else -> {
                            Log.e(mTAG, "Unknown intent")
                        }
                    }


                }



            }
        }


        if (!isRegister) {
            filter = IntentFilter()
            filter.addAction(Constants.ACTION.ACTION_LOGIN_ACTION)
            filter.addAction(Constants.ACTION.ACTION_LOGIN_NETWORK_ERROR)
            filter.addAction(Constants.ACTION.ACTION_LOGIN_SERVER_ERROR)
            filter.addAction(Constants.ACTION.ACTION_LOGIN_SUCCESS)
            filter.addAction(Constants.ACTION.ACTION_LOGIN_FAILED)
            //filter.addAction(Constants.ACTION.ACTION_SIGNIN_ACTION)
            //filter.addAction(Constants.ACTION.ACTION_SIGNIN_SUCCESS)
            filter.addAction(Constants.ACTION.ACTION_LOGOUT_ACTION)
            filter.addAction(Constants.ACTION.ACTION_HIDE_KEYBOARD)
            filter.addAction(Constants.ACTION.ACTION_MAP_HIDE_CLEAR)
            filter.addAction(Constants.ACTION.ACTION_MAP_SHOW_CLEAR)
            filter.addAction(Constants.ACTION.ACTION_PUNCH_CARD_ACTION)
            mContext!!.registerReceiver(mReceiver, filter)
            isRegister = true
            Log.d(mTAG, "registerReceiver mReceiver")
        }
    }

    override fun onResume() {
        super.onResume()
        Log.i(mTAG, "onResume")
    }

    override fun onPause() {
        super.onPause()

        Log.i(mTAG, "onPause")
    }

    override fun onDestroy() {
        Log.i(mTAG, "onDestroy")

        if (isRegister && mReceiver != null) {
            try {
                mContext!!.unregisterReceiver(mReceiver)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }

            isRegister = false
            mReceiver = null
            Log.d(mTAG, "unregisterReceiver mReceiver")
        }

        super.onDestroy()
    }

    override fun onBackPressed() {
        val confirmdialog = AlertDialog.Builder(this@MainActivity)
        confirmdialog.setIcon(R.drawable.baseline_exit_to_app_black_48)
        confirmdialog.setTitle(resources.getString(R.string.exit_app_title))
        confirmdialog.setMessage(resources.getString(R.string.exit_app_msg))
        confirmdialog.setPositiveButton(
            resources.getString(R.string.confirm)
        ) { _, _ ->
            val drawer : DrawerLayout = findViewById(R.id.drawer_layout)
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START)
            }

            //isLogin = false

            finish()
        }
        confirmdialog.setNegativeButton(
            resources.getString(R.string.cancel)
        ) { _, _ ->
            // btnScan.setVisibility(View.VISIBLE);
            // btnConfirm.setVisibility(View.GONE);
        }
        confirmdialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)

        keyboard = menu.findItem(R.id.main_hide_or_show_keyboard)
        locate = menu.findItem(R.id.main_my_locate)
        clearHistory = menu.findItem(R.id.main_clear_history)

        when(currentFrag) {
            CurrentFragment.LOGIN_FRAGMENT -> {
                keyboard!!.isVisible = true
                locate!!.isVisible = false
                clearHistory!!.isVisible = false
            }
            CurrentFragment.PUNCHCARD_FRAGMENT -> {
                keyboard!!.isVisible = false
                locate!!.isVisible = true
                clearHistory!!.isVisible = false
            }
            CurrentFragment.HISTORY_FRAGMENT -> {
                keyboard!!.isVisible = false
                locate!!.isVisible = false
                clearHistory!!.isVisible = true
            }

            /*else -> {
                keyboard!!.isVisible = false
                locate!!.isVisible = false
                clearHistory!!.isVisible = false
            }*/
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        when (item.itemId) {
            R.id.main_hide_or_show_keyboard -> {
                imm?.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0)
            }
            R.id.main_my_locate -> {
                Log.e(mTAG, "menu main_my_locate click")
                val locateIntent = Intent()
                locateIntent.action = Constants.ACTION.ACTION_MAP_MARKER_CLEAR
                sendBroadcast(locateIntent)

                //fabClear!!.visibility = View.GONE
                fabClear!!.hide()
            }
            R.id.main_clear_history -> {
                Log.e(mTAG, "menu main_clear_history click")

                val confirmdialog = AlertDialog.Builder(this@MainActivity)
                confirmdialog.setIcon(R.drawable.baseline_warning_black_48)
                confirmdialog.setTitle(resources.getString(R.string.history_clear_title))
                confirmdialog.setMessage(resources.getString(R.string.history_clear_all_history))
                confirmdialog.setPositiveButton(
                    resources.getString(R.string.confirm)
                ) { _, _ ->
                    //clear db
                    if (db != null) {

                        if (historyList != null) {
                            if (historyList!!.size > 0) {
                                for (historyItem : History in historyList as ArrayList) {
                                    Log.e(mTAG, "remove id="+historyItem.getId()+" desc = "+historyItem.getDesc()+" date = "+historyItem.getDate()+" time = "+historyItem.getTime())
                                    db!!.historyDao().delete(historyItem)
                                }
                            }
                        }
                    } else {
                        Log.e(mTAG, "db = null")
                    }

                    historyList!!.clear()

                    val locateIntent = Intent()
                    locateIntent.action = Constants.ACTION.ACTION_HISTORY_CLEAR_SUCCESS
                    sendBroadcast(locateIntent)
                }
                confirmdialog.setNegativeButton(
                    resources.getString(R.string.cancel)
                ) { _, _ ->
                    // btnScan.setVisibility(View.VISIBLE);
                    // btnConfirm.setVisibility(View.GONE);
                }
                confirmdialog.show()



            }

            else -> super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        selectDrawerItem(item)
        /*when (item.itemId) {
            R.id.nav_home -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_tools -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)*/
        return true
    }

    private fun selectDrawerItem(menuItem: MenuItem) {
        var fragment: Fragment? = null
        var fragmentClass: Class<*>? = null

        var title = ""
        //hide keyboard
        val view = currentFocus

        if (view != null) {
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }

        //hide fab button
        //fabClear!!.visibility = View.GONE
        fabClear!!.hide()

        /*var statusTitle = ""
        when(printerStatus) {
            BluetoothChatService.STATE_NONE, BluetoothChatService.STATE_LISTEN-> {
                statusTitle = getString(R.string.tag_printer_status_not_connected)
            }
            BluetoothChatService.STATE_CONNECTING-> {
                statusTitle = getString(R.string.tag_printer_status_connecting)
            }
            BluetoothChatService.STATE_CONNECTED-> {
                statusTitle = getString(R.string.tag_printer_status_connected)
            }

        }*/

        when (menuItem.itemId) {
            R.id.nav_go_to_work -> {
                /*menuItemKeyboard!!.isVisible = true
                menuItemBluetooth!!.isVisible = true*/



                title = getString(R.string.nav_punchcard)
                fragmentClass = FragmentPunchCard::class.java
                currentFrag = CurrentFragment.PUNCHCARD_FRAGMENT
                keyboard!!.isVisible = false
                locate!!.isVisible = true
                clearHistory!!.isVisible = false
            }
            R.id.nav_history -> {
                fragmentClass = FragmentHistory::class.java
                currentFrag = CurrentFragment.HISTORY_FRAGMENT
                keyboard!!.isVisible = false
                locate!!.isVisible = false
                clearHistory!!.isVisible = true
                /*menuItemKeyboard!!.isVisible = true
                menuItemBluetooth!!.isVisible = true
                title = getString(R.string.nav_storage) +" - "+ statusTitle
                fragmentClass = StorageFragment::class.java
                menuItem.isChecked = true
                currentFrag = CurrentFragment.STORAGE_FRAGMENT*/
            }
            R.id.nav_login -> {
                //menuItemKeyboard!!.isVisible = true
                //menuItemBluetooth!!.isVisible = true
                title = getString(R.string.nav_login)
                fragmentClass = FragmentLogin::class.java
                currentFrag = CurrentFragment.LOGIN_FRAGMENT

                keyboard!!.isVisible = true
                locate!!.isVisible = false
                clearHistory!!.isVisible = false
            }
            R.id.nav_logout -> {

                showLogoutConfirmDialog()

                /*title = getString(R.string.nav_logout)
                fragmentClass = FragmentLogout::class.java
                currentFrag = CurrentFragment.LOGOUT_FRAGMENT

                keyboard!!.isVisible = false
                locate!!.isVisible = false
                clearHistory!!.isVisible = false*/
            }
            R.id.nav_about -> {
                showCurrentVersionDialog()
            }
        }

        try {
            fragment = fragmentClass?.newInstance() as Fragment
        } catch (e: Exception) {
            e.printStackTrace()
            val drawer: DrawerLayout = findViewById(R.id.drawer_layout)
            drawer.closeDrawer(GravityCompat.START)
        }

        if (fragment != null) {
            // Insert the fragment by replacing any existing fragment
            val fragmentManager = supportFragmentManager
            //fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commitAllowingStateLoss()

            // Highlight the selected item has been done by NavigationView

            // Set action bar title
            if (title.isNotEmpty())
                setTitle(title)
            else
                setTitle(menuItem.title)
            // Close the navigation drawer
            val drawer: DrawerLayout = findViewById(R.id.drawer_layout)
            drawer.closeDrawer(GravityCompat.START)
        }
    }



    fun toast(message: String) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL, 0, 0)
        val group = toast.view as ViewGroup
        group.setBackgroundResource(R.drawable.toast_corner_round)
        val textView = group.getChildAt(0) as TextView
        //textView.setTextSize(25.0f)
        textView.textSize = 25.0f
        toast.show()
    }


    /*private fun getDeviceImei() {

        mTelephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                imei = mTelephonyManager!!.imei

            } catch (e: SecurityException) {
                e.printStackTrace()
            }

        } else {
            try {

                imei = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mTelephonyManager!!.imei
                } else {
                    mTelephonyManager!!.getDeviceId()
                }

                //fragStaffinfoWifiId!!.setText(IMEI)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }



    }*/



    fun callAPILogin(StaffNum: String, password: String) {
        val para = HttpUserAuthPara()
        para.m01 = StaffNum
        para.m02 = password
        ApiFunc().login(para, loginCallback)
    }//login

    private var loginCallback: Callback = object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e(mTAG, " e = $e")
            runOnUiThread(netErrRunnable)
        }

        @Throws(IOException::class)
        override fun onResponse(call: Call, response: Response) {
            val res = ReceiveTransform.restoreToJsonStr(response.body!!.string())
            Log.e(mTAG, "res = $res")
            runOnUiThread {
                try {
                    val rjUser = Gson().fromJson(res, PCUser::class.java)
                    if (rjUser.result == "0") {
                        //fail
                        //mLoadingView.setStatus(LoadingView.GONE)
                        // Toast.makeText(mContext,rjUser.tc_zx104,Toast.LENGTH_LONG).show();
                        //showMyToast(rjUser.result2, mContext)
                        //Thread.sleep(3000)
                        if (rjUser.result2.equals("裝置未註冊!")) {

                            toast(rjUser.result2 as String)

                            val notsigninIntent = Intent()
                            notsigninIntent.action = Constants.ACTION.ACTION_LOGIN_NOT_SIGNIN
                            sendBroadcast(notsigninIntent)
                        } else {
                            val failIntent = Intent()
                            failIntent.action = Constants.ACTION.ACTION_LOGIN_FAILED
                            sendBroadcast(failIntent)
                        }


                    } else {
                        //success
                        Log.e(mTAG, "loginCallback success")

                        username = rjUser.result2 as String

                        val successIntent = Intent()
                        successIntent.action = Constants.ACTION.ACTION_LOGIN_SUCCESS
                        sendBroadcast(successIntent)


                    }

                }// try
                catch (e: Exception) {
                    //mLoadingView.setStatus(LoadingView.GONE)
                    //Toast.makeText(mContext,getString(R.string.toast_server_error),Toast.LENGTH_LONG).show();
                    //showMyToast(getString(R.string.toast_server_error), mContext)
                    e.printStackTrace()
                    toast(getString(R.string.toast_server_error))
                    val failIntent = Intent()
                    failIntent.action = Constants.ACTION.ACTION_LOGIN_FAILED
                    sendBroadcast(failIntent)
                }
            }
        }//response
    }

    /*fun callAPISign(StaffNum: String, IMEI: String, pwd: String) {
        val para = HttpUserAuthPara()
        para.m01 = StaffNum
        para.m02 = IMEI
        para.m03 = pwd
        ApiFunc().sign(para, signCallback)
    }//login

    private var signCallback: Callback = object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            runOnUiThread(netErrRunnable)
        }

        @Throws(IOException::class)
        override fun onResponse(call: Call, response: Response) {
            val res = ReceiveTransform.restoreToJsonStr(response.body!!.string())

            Log.e(mTAG, "res = $res")

            runOnUiThread {
                try {
                    val rjUser = Gson().fromJson(res, PCUser::class.java)
                    if (rjUser.result == "0") {
                        //fail

                        toast(rjUser.result2 as String)

                        if (rjUser.result2.equals("此裝置已有有效的員工/裝置認證")) {
                            val failIntent = Intent()
                            failIntent.action = Constants.ACTION.ACTION_SIGNIN_ALREADY
                            sendBroadcast(failIntent)
                        } else {
                            val failIntent = Intent()
                            failIntent.action = Constants.ACTION.ACTION_SIGNIN_FAILED
                            sendBroadcast(failIntent)
                        }


                    } else {
                        //success

                        toast(getString(R.string.signin_success))

                        val successIntent = Intent()
                        successIntent.action = Constants.ACTION.ACTION_SIGNIN_SUCCESS
                        sendBroadcast(successIntent)
                    }

                }// try
                catch (e: Exception) {
                    e.printStackTrace()
                    toast(getString(R.string.toast_server_error))
                    val failIntent = Intent()
                    failIntent.action = Constants.ACTION.ACTION_SIGNIN_FAILED
                    sendBroadcast(failIntent)
                }
            }
        }//response
    }*/

    //fun callAPIPunch(StaffNum: String, date: String, time: String, code: String, imei: String, latitude: String, longitude: String) {
    fun callAPIPunch(StaffNum: String, date: String, time: String, code: String, mobile: String, latitude: String, longitude: String) {


        Log.e(mTAG, "currentCode = $currentCode")

        val para = HttpPunchPara()
        para.cqr01 = StaffNum
        para.cqr02 = date
        para.cqr03 = time
        para.cqr04 = code
        para.cqrmobile = mobile
        para.cqrlatit = latitude
        para.cqrlongit = longitude
        ApiFunc().punch(para, punchCallback)
    }//login

    private var punchCallback: Callback = object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            runOnUiThread(netErrRunnable)
        }

        @Throws(IOException::class)
        override fun onResponse(call: Call, response: Response) {
            val res = ReceiveTransform.restoreToJsonStr(response.body!!.string())

            Log.e(mTAG, "res = $res")

            runOnUiThread {
                try {
                    val rjUser = Gson().fromJson(res, PCUser::class.java)
                    if (rjUser.result == "0") {
                        //fail

                       toast(rjUser.result2 as String)

                        val failIntent = Intent()
                        failIntent.action = Constants.ACTION.ACTION_PUNCH_CARD_FAILED
                        sendBroadcast(failIntent)
                        /*
                        if (rjUser.result2.equals("此裝置已有有效的員工/裝置認證")) {
                            val failIntent = Intent()
                            failIntent.action = Constants.ACTION.ACTION_SIGNIN_ALREADY
                            sendBroadcast(failIntent)
                        } else {
                            val failIntent = Intent()
                            failIntent.action = Constants.ACTION.ACTION_SIGNIN_FAILED
                            sendBroadcast(failIntent)
                        }*/


                    } else {
                        //success

                        val resultArray: List<String> = rjUser.result2!!.split(":")


                        for (i in resultArray.indices) { //indices = multi index
                        //for (i in 0..resultArray.size-1) {
                            Log.e(mTAG, "resultArray[$i] = "+resultArray[i])
                        }

                        val successIntent = Intent()
                        successIntent.action = Constants.ACTION.ACTION_PUNCH_CARD_SUCCESS
                        successIntent.putExtra("CODE", currentCode)
                        if (resultArray.size == 3) {
                            successIntent.putExtra("TIME_HOURS", resultArray[1])
                            successIntent.putExtra("TIME_MINUTES", resultArray[2])
                        }
                        sendBroadcast(successIntent)
                    }

                }// try
                catch (e: Exception) {
                    e.printStackTrace()
                    toast(getString(R.string.toast_server_error))
                    val failIntent = Intent()
                    failIntent.action = Constants.ACTION.ACTION_PUNCH_CARD_SERVER_ERROR
                    sendBroadcast(failIntent)
                }
            }
        }//response
    }

    internal var netErrRunnable: Runnable = Runnable {
        //mLoadingView.setStatus(LoadingView.GONE)
        // Toast.makeText(mContext,getString(R.string.toast_network_error),Toast.LENGTH_LONG).show();
        toast(getString(R.string.toast_network_error))
        val failIntent = Intent()
        failIntent.action = Constants.ACTION.ACTION_NETWORK_FAILED
        sendBroadcast(failIntent)
    }

    private fun showLogoutConfirmDialog() {

        // get prompts.xml view
        /*LayoutInflater layoutInflater = LayoutInflater.from(Nfc_read_app.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);*/
        val promptView = View.inflate(this@MainActivity, R.layout.confirm_dialog, null)

        val alertDialogBuilder = AlertDialog.Builder(this@MainActivity).create()
        alertDialogBuilder.setView(promptView)

        //final EditText editFileName = (EditText) promptView.findViewById(R.id.editFileName);
        val textViewMsg = promptView.findViewById<TextView>(R.id.textViewDialog)
        val btnCancel = promptView.findViewById<Button>(R.id.btnDialogCancel)
        val btnConfirm = promptView.findViewById<Button>(R.id.btnDialogConfirm)

        textViewMsg.text = getString(R.string.logout_title_msg)
        btnCancel.text = getString(R.string.cancel)
        btnConfirm.text = getString(R.string.confirm)

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
        btnCancel!!.setOnClickListener {
            /*if (currentSelectMenuItem != null) {
                currentSelectMenuItem!!.isChecked = false
            }*/

            alertDialogBuilder.dismiss()
        }
        btnConfirm!!.setOnClickListener {
            val drawer: DrawerLayout = findViewById(R.id.drawer_layout)
            drawer.closeDrawer(GravityCompat.START)

            val logoutIntent = Intent(Constants.ACTION.ACTION_LOGOUT_ACTION)
            mContext?.sendBroadcast(logoutIntent)
            alertDialogBuilder.dismiss()
        }
        alertDialogBuilder.show()


    }

    private fun showCurrentVersionDialog() {

        // get prompts.xml view
        /*LayoutInflater layoutInflater = LayoutInflater.from(Nfc_read_app.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);*/
        val promptView = View.inflate(this@MainActivity, R.layout.about_dialog, null)

        val alertDialogBuilder = AlertDialog.Builder(this@MainActivity).create()
        alertDialogBuilder.setView(promptView)

        //final EditText editFileName = (EditText) promptView.findViewById(R.id.editFileName);
        val textViewMsg = promptView.findViewById<TextView>(R.id.textViewDialog)
        val textViewFixMsg = promptView.findViewById<TextView>(R.id.textViewFixHistory)
        val btnCancel = promptView.findViewById<Button>(R.id.btnDialogCancel)
        val btnConfirm = promptView.findViewById<Button>(R.id.btnDialogConfirm)

        textViewMsg.text = getString(R.string.version_string, BuildConfig.VERSION_CODE, BuildConfig.VERSION_NAME)
        val msg = "1. 修正登入方式"
        //msg += "2. 解決重複兩次barcode造成無法確認的問題\n"
        //msg += "3. 新增\"設定\"讓使用者決定手動或自動確認"
        textViewFixMsg.text = msg

        btnCancel.text = getString(R.string.cancel)
        btnCancel.visibility = View.GONE
        btnConfirm.text = getString(R.string.confirm)

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)

        btnConfirm!!.setOnClickListener {

            alertDialogBuilder.dismiss()
        }
        alertDialogBuilder.show()

    }
}
