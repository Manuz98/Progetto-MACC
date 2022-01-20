package com.example.mobileproject

import android.util.Log
import com.example.mobileproject.Common.Common
import java.io.InputStreamReader
import java.lang.Exception
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class Proxy {
    fun request(): String {
        val name =
            "https://donika29.pythonanywhere.com/request?regione="+ Common.currentRegion
        val url = URL(name)
        val conn = url.openConnection() as HttpsURLConnection
        try {

            conn.run {
                requestMethod = "POST"
                Log.i("POLL", "ok")
                return InputStreamReader(inputStream).readText()


            }
        } catch (e: Exception) {
            e.printStackTrace();
            Log.v("POLL", e.toString())
            return "error"
        }

    }

    fun update(): String {
        val name =
            "https://donika29.pythonanywhere.com/request?regione="+ Common.currentRegion
        val url = URL(name)
        val conn = url.openConnection() as HttpsURLConnection
        try {

            conn.run {
                requestMethod = "PUT"
                Log.i("POLL", "ok")
                return InputStreamReader(inputStream).readText()


            }
        } catch (e: Exception) {
            e.printStackTrace();
            Log.v("POLL", e.toString())
            return "error"
        }

    }

    fun delete(): String {
        val name =
            "https://donika29.pythonanywhere.com/delete?regione="+ Common.currentRegion
        val url = URL(name)
        val conn = url.openConnection() as HttpsURLConnection
        try {

            conn.run {
                requestMethod = "POST"
                Log.i("POLL", InputStreamReader(inputStream).readText())
                return InputStreamReader(inputStream).readText()


            }
        } catch (e: Exception) {
            e.printStackTrace();
            Log.v("POLL", e.toString())
            return "error"
        }

    }
}