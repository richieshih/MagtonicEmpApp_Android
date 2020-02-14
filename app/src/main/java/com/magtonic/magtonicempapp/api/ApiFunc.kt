package com.magtonic.magtonicempapp.api

import android.util.Log
import com.google.gson.Gson
import com.magtonic.magtonicempapp.model.send.HttpPunchPara
import com.magtonic.magtonicempapp.model.send.HttpUserAuthPara
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.HashMap

class ApiFunc {
    //private val TAG = ApiFunc::class.java.name

    //val baseIP = "http://192.1.1.42:81/asmx/WebService.asmx/"

    private val baseIP = "http://61.216.114.217/asmx/WebService.asmx/"

    private val apiStrLogin = baseIP + "Chk_mobile"

    private val apiStrSign = baseIP + "Ins_mobile_file"

    //private val apiStrPC = baseIP + "Ins_mobile"

    private val apiStrSignWithLatLng = baseIP + "Ins_mobile3"

    object ContentType {

        const val title = "Content-Type"
        //const val Json = "application/json; charset=utf-8"
        const val xxxForm = "application/x-www-form-urlencoded"

    }//ContentType

    fun login(para: HttpUserAuthPara, callback: Callback) {
        val paraMap = HashMap<String, String>()
        paraMap["p_json"] = Gson().toJson(para)
        postWithMultiKey(apiStrLogin, paraMap, callback)

    }//login

    fun sign(para: HttpUserAuthPara, callback: Callback) {
        val paraMap = HashMap<String, String>()
        paraMap["p_json"] = Gson().toJson(para)
        postWithMultiKey(apiStrSign, paraMap, callback)

    }//sign

    fun punch(para: HttpPunchPara, callback: Callback) {
        val paraMap = HashMap<String, String>()
        paraMap["p_json"] = Gson().toJson(para)
        postWithMultiKey(apiStrSignWithLatLng, paraMap, callback)
    }

    /*fun PC(para: HttpPCPara, callback: Callback) {
        val paraMap = HashMap<String, String>()
        paraMap["p_json"] = Gson().toJson(para)
        postWithMultiKey(apiStrPC, paraMap, callback)
    }*/

    private fun postWithMultiKey(url: String, paraMap: HashMap<String, String>, callback: Callback) {

        val builder = FormBody.Builder()

        for (key in paraMap.keys) {
            builder.add(key, paraMap[key] as String)
        }

        val body = builder.build()
        val rBulider = Request.Builder()
        rBulider.url(url)
        rBulider.post(body)
        rBulider.addHeader(ContentType.title, ContentType.xxxForm)
        val req = rBulider.build()

        val client = OkHttpClient()

        try {
            val response = client.newCall(req).enqueue(callback)
            Log.e("postWithMultiKey", "response = $response")

        } catch (e: IOException) {
            e.printStackTrace()
        }

        //MobilePCApp.okHttpClient.newCall(req).enqueue(callback)

    }
}