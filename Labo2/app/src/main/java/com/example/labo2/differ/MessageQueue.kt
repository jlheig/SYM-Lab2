package com.example.labo2.differ

import android.os.Handler
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
class MessageQueue {
    private val TAG = "MessageQueue"
    private var requestQueue: Queue<String> = PriorityQueue<String>()
    private var firstRequest = true; //Used to not start a thread everytime sendRequest is called
    fun sendRequest(request : String, urlName : String ) {
        requestQueue.add(request)
        if (firstRequest) {
            processRequest(urlName)
        }
    }

    fun processRequest(urlName: String){
        Thread {
            firstRequest = false;
            val currentRequest = requestQueue.peek() ?: return@Thread;

            val url = URL(urlName)
            val urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "POST"
            urlConnection.setRequestProperty("content-type", "text/plain")
            urlConnection.connectTimeout = 5000;

            try {
                val output = OutputStreamWriter(urlConnection.outputStream)
                output.write(currentRequest)
                output.flush()

                val input = InputStreamReader(urlConnection.inputStream)
                var text = input.readText()
                Log.i(TAG, "server send: $text")
                requestQueue.poll();
            }
            catch (e: SocketTimeoutException) {
                Log.e(TAG, e.toString())

            }
            catch (e: IOException){
                Log.e(TAG, e.toString())
            }
            finally {
                urlConnection.disconnect()
                Thread.sleep(5000)
                processRequest(urlName)
            }
        }.start()
    }
}