package com.magtonic.magtonicempapp.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

import android.graphics.Rect


import android.os.Bundle

//import android.support.v4.app.Fragment
import androidx.fragment.app.Fragment

import android.util.Log
import android.view.*
import android.widget.*
import com.magtonic.magtonicempapp.MainActivity
import com.magtonic.magtonicempapp.MainActivity.Companion.imei
import com.magtonic.magtonicempapp.MainActivity.Companion.isKeyBoardShow
import com.magtonic.magtonicempapp.R

import com.magtonic.magtonicempapp.data.Constants


class FragmentLogin: Fragment() {
    private val mTag = FragmentLogin::class.java.name
    private var loginContext: Context? = null

    private var mReceiver: BroadcastReceiver? = null
    private var isRegister = false

    //private var mTelephonyManager: TelephonyManager? = null
    //private var IMEI: String? = null
    //private var StaffNum: String? = null
    //private var PWD: String? = null

    //private var fragStaffInfoWifiId: TextView?= null
    //var fragStaffinfoWifiStatus: TextView? = null

    private var acFirstTimeUseStaffNum: EditText? = null
    private var acFirstTimeUsePassword: EditText? = null
    private var acFirstTimeUseLoginButton: Button? = null
    private var acFirstTimeUseSignButton: Button? = null

    //var mWifiManager: WifiManager? = null

    //private var mWifiTools: WifiTools? = null
    //private val mWifiInfo: WifiInfo? = null
    //internal var SSID = "ERP06"
    //internal var WifiPwd = "T69924056onic"
    //private var mWifiScannThread: Thread? = null

    //private val WIFI_IS_ERP06 = 1
    //private val WIFI_ISNT_ERP06 = 2
    //private var nowUseWifiSSID = ""
    //private var nowUseWifiNetid: Int = 0

    private var progressBar: ProgressBar? = null
    private var relativeLayout: RelativeLayout? = null
    private var linearLayout: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e(mTag, "onCreate")

        loginContext = context



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d(mTag, "onCreateView")

        val view = inflater.inflate(R.layout.fragment_login, container, false)

        relativeLayout = view.findViewById(R.id.login_container)
        progressBar = ProgressBar(loginContext, null, android.R.attr.progressBarStyleLarge)
        val params = RelativeLayout.LayoutParams(MainActivity.screenHeight / 4, MainActivity.screenWidth / 4)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)
        relativeLayout!!.addView(progressBar, params)
        progressBar!!.visibility = View.GONE

        //detect soft keyboard
        linearLayout = view.findViewById(R.id.linearLayoutLogin)
        linearLayout!!.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            linearLayout!!.getWindowVisibleDisplayFrame(r)
            //val screenHeight = linearLayout!!.getRootView().getHeight()
            val screenHeight = linearLayout!!.rootView.height
            val keypadHeight = screenHeight - r.bottom
            isKeyBoardShow = (keypadHeight > screenHeight * 0.15)
        }

        val textViewTitle : TextView = view.findViewById(R.id.first_load_textView_title2)
        //fragStaffInfoWifiId = view.findViewById(R.id.frag_staffinfo_wifi_id)
        //fragStaffinfoWifiStatus = view.findViewById(R.id.frag_staffinfo_wifi_status)
        acFirstTimeUseStaffNum = view.findViewById(R.id.ac_first_time_use_staff_num)
        acFirstTimeUsePassword = view.findViewById(R.id.ac_first_time_use_password)
        acFirstTimeUseLoginButton = view.findViewById(R.id.ac_first_time_use_login_button)
        //acFirstTimeUseSignButton = view.findViewById(R.id.ac_first_time_use_sign_button)

        val title1 = getString(R.string.first_company_name)
        val title2 = getString(R.string.loadingTitle2)
        val logintitle = title1 + title2
        textViewTitle.text = logintitle

        /*mWifiManager = loginContext!!.getSystemService(Context.WIFI_SERVICE) as WifiManager

        if (!mWifiManager!!.isWifiEnabled) {
            mWifiManager!!.isWifiEnabled = true
        }*/

        //getDeviceImei()
        //fragStaffInfoWifiId!!.text =  getString(R.string.login_auth_code, imei)

        /*mWifiScannThread = WifiScannThread()
        mWifiScannThread!!.start()
        mWifiTools = WifiTools(mWifiManager as WifiManager)*/

        acFirstTimeUseLoginButton!!.setOnClickListener {
            progressBar!!.visibility = View.VISIBLE
            acFirstTimeUseLoginButton!!.visibility= View.INVISIBLE

            val account: EditText? = acFirstTimeUseStaffNum
            val password: EditText? = acFirstTimeUsePassword
            if (account != null && password != null) {
                if (account.text.isEmpty()) {
                    progressBar!!.visibility = View.GONE
                    acFirstTimeUseLoginButton!!.visibility= View.VISIBLE
                    if (isAdded) {
                        toast(resources.getString(R.string.login_account_empty))
                    }
                } else if (password.text.isEmpty()) {
                    progressBar!!.visibility = View.GONE
                    acFirstTimeUseLoginButton!!.visibility= View.VISIBLE
                    if (isAdded) {
                        toast(resources.getString(R.string.login_password_empty))
                    }
                } else {
                    Log.d(mTag, "no other ")
                    val loginIntent = Intent()
                    loginIntent.action = Constants.ACTION.ACTION_LOGIN_ACTION
                    loginIntent.putExtra("account", account.text.toString())
                    loginIntent.putExtra("password", password.text.toString())
                    //loginIntent.putExtra("imei", imei)
                    loginContext!!.sendBroadcast(loginIntent)



                }
            }
        }

        /*acFirstTimeUseSignButton!!.setOnClickListener {
            progressBar!!.visibility = View.VISIBLE
            acFirstTimeUseSignButton!!.visibility= View.INVISIBLE

            val account: EditText? = acFirstTimeUseStaffNum
            val password: EditText? = acFirstTimeUsePassword
            if (account != null && password != null) {
                if (account.text.isEmpty()) {
                    progressBar!!.visibility = View.GONE
                    acFirstTimeUseSignButton!!.visibility= View.VISIBLE
                    if (isAdded) {
                        toast(resources.getString(R.string.login_account_empty))
                    }
                } else if (password.text.isEmpty()) {
                    progressBar!!.visibility = View.GONE
                    acFirstTimeUseSignButton!!.visibility= View.VISIBLE
                    if (isAdded) {
                        toast(resources.getString(R.string.login_password_empty))
                    }
                } else {
                    Log.d(mTag, "no other ")
                    val signinIntent = Intent()
                    signinIntent.action = Constants.ACTION.ACTION_SIGNIN_ACTION
                    signinIntent.putExtra("account", account.text.toString())
                    signinIntent.putExtra("password", password.text.toString())
                    //loginIntent.putExtra("imei", imei)
                    loginContext!!.sendBroadcast(signinIntent)



                }
            }
        }*/

        val filter: IntentFilter

        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                if (intent.action != null) {
                   when (intent.action) {
                       Constants.ACTION.ACTION_NETWORK_FAILED -> {
                           Log.d(mTag, "ACTION_NETWORK_FAILED")

                           progressBar!!.visibility = View.GONE
                           acFirstTimeUseLoginButton!!.visibility = View.VISIBLE
                           acFirstTimeUseSignButton!!.visibility = View.VISIBLE
                       }
                       Constants.ACTION.ACTION_LOGIN_NOT_SIGNIN -> {
                           Log.d(mTag, "ACTION_LOGIN_NOT_SIGNIN")

                           //Log.e(mTag, "account = $account, password = $password, imei = $imei")
                           progressBar!!.visibility = View.GONE
                           acFirstTimeUseLoginButton!!.visibility = View.VISIBLE
                       }
                       Constants.ACTION.ACTION_LOGIN_FAILED -> {
                           Log.d(mTag, "ACTION_LOGIN_FAILED")

                           //Log.e(mTag, "account = $account, password = $password, imei = $imei")
                           progressBar!!.visibility = View.GONE
                           acFirstTimeUseLoginButton!!.visibility = View.VISIBLE
                       }
                       Constants.ACTION.ACTION_SIGNIN_FAILED -> {
                           Log.d(mTag, "ACTION_SIGNIN_FAILED")

                           progressBar!!.visibility = View.GONE
                           acFirstTimeUseSignButton!!.visibility = View.VISIBLE
                       }
                       Constants.ACTION.ACTION_SIGNIN_ALREADY -> {
                           Log.d(mTag, "ACTION_SIGNIN_ALREADY")

                           progressBar!!.visibility = View.GONE
                           acFirstTimeUseSignButton!!.visibility = View.VISIBLE
                       }
                   }
                }
            }
        }


        if (!isRegister) {
            filter = IntentFilter()
            filter.addAction(Constants.ACTION.ACTION_NETWORK_FAILED)
            filter.addAction(Constants.ACTION.ACTION_LOGIN_NOT_SIGNIN)
            filter.addAction(Constants.ACTION.ACTION_LOGIN_FAILED)
            filter.addAction(Constants.ACTION.ACTION_SIGNIN_FAILED)
            filter.addAction(Constants.ACTION.ACTION_SIGNIN_ALREADY)
            loginContext?.registerReceiver(mReceiver, filter)
            isRegister = true
            Log.d(mTag, "registerReceiver mReceiver")
        }

        return view
    }

    override fun onDestroyView() {
        Log.i(mTag, "onDestroy")

        if (isRegister && mReceiver != null) {
            try {
                loginContext!!.unregisterReceiver(mReceiver)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }

            isRegister = false
            mReceiver = null
            Log.d(mTag, "unregisterReceiver mReceiver")
        }

        super.onDestroyView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.i(mTag, "onActivityCreated")
        super.onActivityCreated(savedInstanceState)

    }

    private fun toast(message: String) {
        val toast = Toast.makeText(loginContext, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL, 0, 0)
        val group = toast.view as ViewGroup
        group.setBackgroundResource(R.drawable.toast_corner_round)
        val textView = group.getChildAt(0) as TextView
        textView.textSize = 25.0f
        toast.show()
    }

    /*private fun setWifiInfoparm(wifissid: String, wifinetid: Int) {


        try {
            if (wifissid.startsWith("\"") && wifissid.endsWith("\"")) {
                this.nowUseWifiSSID = wifissid.substring(1, wifissid.length - 1)
            }
            this.nowUseWifiNetid = wifinetid


        } catch (ex: Exception) {

        }

    }

    private fun CheckERP06(): Boolean {

        //val ret = nowUseWifiSSID == SSID

        //Log.e(mTag, "CheckERP06: nowUseWifiSSID = "+nowUseWifiSSID+", SSID = "+SSID+" ret = "+ret)

        return nowUseWifiSSID == SSID
    }

    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {

            super.handleMessage(msg)
            when (msg.what) {
                WIFI_IS_ERP06 -> {
                    try {

                    fragStaffinfoWifiStatus!!.setText(nowUseWifiSSID)
                    } catch (e: Exception) {
                    }
                }


                WIFI_ISNT_ERP06 -> try {
                      fragStaffinfoWifiStatus.setText(nowUseWifiSSID);
                        MobilePCApp.mWifiManager.disableNetwork(nowUseWifiNetid);
                        MobilePCApp.mWifiManager.removeNetwork(nowUseWifiNetid);
                        MobilePCApp.mWifiManager.saveConfiguration();
                        MobilePCApp.mWifiManager.setWifiEnabled(false);
                        fragStaffinfoWifiStatus.setText(nowUseWifiSSID);

                        Thread.sleep(3000);

                        MobilePCApp.mWifiManager.setWifiEnabled(true);
                        mWifiTools.connect(mWifiInfo.SSID, mWifiInfo.WifiPwd, WifiTools.WifiCipherType.WIFICIPHER_WPA);

                        Thread.sleep(5000);

                        fragStaffinfoWifiStatus.setText(nowUseWifiSSID);
                } catch (ie: Exception) {
                }

            }
        }
    }



    internal inner class WifiScannThread : Thread() {
        override fun run() {
            super.run()
            setWifiInfoparm(
                mWifiManager!!.getConnectionInfo().getSSID(),
                mWifiManager!!.getConnectionInfo().getNetworkId()
            )
            while (true) {
                val msg = Message()
                if (CheckERP06()) {
                    msg.what = 1
                    mHandler.sendMessage(msg)
                    try {
                        Thread.sleep(1000)
                    } catch (e: Exception) {
                    }

                } else {
                    msg.what = 2
                    mHandler.sendMessage(msg)
                    try {
                        Thread.sleep(10000)
                    } catch (e: Exception) {
                    }

                }


            }

        }
    }*/

    /*private fun attemptLogin() {

        StaffNum = acFirstTimeUseStaffNum!!.getText().toString().trim({ it <= ' ' })
        PWD = acFirstTimeUsePassword!!.getText().toString().trim({ it <= ' ' })

        var cancel = false
        var focusView: View? = null


        // Check for a valid account address.
        if (TextUtils.isEmpty(StaffNum)) {
            acFirstTimeUseStaffNum!!.setError(getString(R.string.error_field_required))
            focusView = acFirstTimeUseStaffNum
            cancel = true
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView!!.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //hideKeyboard()
            //mLoadingView.setLoadingStatusWithStr("登入中...請稍候")
            //simulateLogin();
            runOnUiThread {
                callAPILogin(StaffNum, IMEI)
            }

        }// cancel == false ,  call login api

    }// attempt login
    */

}