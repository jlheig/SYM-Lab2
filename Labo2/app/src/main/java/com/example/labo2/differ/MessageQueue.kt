package com.example.labo2.differ

import android.util.Log
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.util.*

/**
 * Class that stock pending messages and try to send them every 5 seconds
 */
class MessageQueue(var firstRequest: Boolean = true) {
    private val TAG = "MessageQueue"
    private var requestQueue: Queue<String> = LinkedList<String>()

    fun sendRequest(request : String, urlName : String ) {
        requestQueue.add(request)
        if(firstRequest) {
            Thread{
                firstRequest = false
                while (!requestQueue.isEmpty()) {
                    processRequest(urlName)
                    Thread.sleep(5000)
                }
                firstRequest = true
            }.start()
        }
    }

    fun processRequest(urlName: String){
        while(true) {
            val currentRequest = requestQueue.peek() ?: return

            val url = URL(urlName)
            val urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "POST"
            urlConnection.setRequestProperty("content-type", "text/plain")
            urlConnection.connectTimeout = 5000

            try {
                val output = OutputStreamWriter(urlConnection.outputStream)
                output.write(currentRequest)
                output.flush()

                val input = InputStreamReader(urlConnection.inputStream)
                val text = input.readText()
                Log.i(TAG, "server send: $text")
                requestQueue.poll()
                urlConnection.disconnect()
            }
            catch (e: Exception){
                Log.e(TAG, "Failed to send messages, retrying in 5 seconds...")
                urlConnection.disconnect()
                return
            }

        }
    }
}