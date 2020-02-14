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
import com.magtonic.magtonicempapp.HistoryDetailActivity
import com.magtonic.magtonicempapp.MainActivity

import com.magtonic.magtonicempapp.MainActivity.Companion.historyList
import com.magtonic.magtonicempapp.R
import com.magtonic.magtonicempapp.data.Constants
import com.magtonic.magtonicempapp.data.HistoryAdapter
import com.magtonic.magtonicempapp.persistence.History


class FragmentHistory: Fragment() {
    private val mTAG = FragmentHistory::class.java.name
    private var historyContext: Context? = null

    private var mReceiver: BroadcastReceiver? = null
    private var isRegister = false

    private var progressBar: ProgressBar? = null
    private var relativeLayout: RelativeLayout? = null
    private var historyAdapter: HistoryAdapter? = null
    private var linearLayout: LinearLayout? = null


    private var listView: ListView?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e(mTAG, "onCreate")

        historyContext = context

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(mTAG, "onCreateView")

        val view = inflater.inflate(R.layout.fragment_history, container, false)

        relativeLayout = view.findViewById(R.id.history_container)
        progressBar = ProgressBar(historyContext, null, android.R.attr.progressBarStyleLarge)
        val params = RelativeLayout.LayoutParams(MainActivity.screenHeight / 4, MainActivity.screenWidth / 4)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)
        relativeLayout!!.addView(progressBar, params)
        progressBar!!.visibility = View.GONE

        //detect soft keyboard
        linearLayout = view.findViewById(R.id.linearLayoutHistory)
        linearLayout!!.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            linearLayout!!.getWindowVisibleDisplayFrame(r)
            //val screenHeight = linearLayout!!.getRootView().getHeight()
            val screenHeight = linearLayout!!.rootView.height
            val keypadHeight = screenHeight - r.bottom
            MainActivity.isKeyBoardShow = (keypadHeight > screenHeight * 0.15)
        }

        listView = view!!.findViewById(R.id.listViewHistory)
        listView!!.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->


            if (historyList != null) {
                if (historyList!!.size > 0) {
                    val intent = Intent(historyContext, HistoryDetailActivity::class.java)
                    intent.putExtra("INDEX", position.toString())
                    startActivity(intent)
                }
            }



        }

        if (historyContext != null) {
            Log.d(mTAG, "historyContext != null")

            if (historyList!!.size == 0) {
                toast(getString(R.string.history_empty))
            }

            historyAdapter = HistoryAdapter(historyContext, R.layout.fragment_history_item, historyList as List<History>)
            listView!!.adapter = historyAdapter
            historyAdapter!!.notifyDataSetChanged()
        }


        val filter: IntentFilter

        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                if (intent.action != null) {
                    when (intent.action) {
                        Constants.ACTION.ACTION_NETWORK_FAILED -> {
                            Log.d(mTAG, "ACTION_NETWORK_FAILED")

                            progressBar!!.visibility = View.GONE

                        }

                        Constants.ACTION.ACTION_HISTORY_CLEAR_SUCCESS -> {
                            Log.d(mTAG, "ACTION_HISTORY_CLEAR_SUCCESS")

                            historyAdapter!!.notifyDataSetChanged()
                        }

                    }
                }
            }
        }


        if (!isRegister) {
            filter = IntentFilter()
            filter.addAction(Constants.ACTION.ACTION_NETWORK_FAILED)
            filter.addAction(Constants.ACTION.ACTION_HISTORY_CLEAR_SUCCESS)
            historyContext?.registerReceiver(mReceiver, filter)
            isRegister = true
            Log.d(mTAG, "registerReceiver mReceiver")
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.i(mTAG, "onActivityCreated")
        super.onActivityCreated(savedInstanceState)

    }

    private fun toast(message: String) {
        val toast = Toast.makeText(historyContext, message, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER, 0, 0)
        val group = toast.view as ViewGroup
        group.setBackgroundResource(R.drawable.toast_corner_round)
        val textView = group.getChildAt(0) as TextView
        textView.textSize = 25.0f
        toast.show()
    }
}