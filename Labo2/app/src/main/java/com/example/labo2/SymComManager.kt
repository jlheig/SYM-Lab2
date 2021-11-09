package com.example.labo2

import android.os.Looper
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.logging.Handler

class SymComManager (private var l : CommunicationEventListener) {

    fun sendRequest(request : String, urlName : String, requestType : String ) {
        Thread {
            val url = URL(urlName)
            val urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "POST"
            urlConnection.setRequestProperty("content-type", requestType)

            try {
                val output = OutputStreamWriter(urlConnection.outputStream)
                output.write(request)
                output.flush()

                val input = InputStreamReader(urlConnection.inputStream)
                var text = input.readText()
                val handler = android.os.Handler(Looper.getMainLooper())
                handler.post {
                    l.handleServerResponse(text)
                }
            } finally {
                urlConnection.disconnect()
            }
        }.start()
    }
}