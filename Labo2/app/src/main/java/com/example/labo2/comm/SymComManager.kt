package com.example.labo2.comm

import android.os.Looper
import java.io.*
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.Deflater
import java.util.zip.DeflaterOutputStream
import java.util.zip.Inflater
import java.util.zip.InflaterInputStream

class SymComManager (private var l : CommunicationEventListener) {

    fun sendRequest(request : String, urlName : String, requestType : String, compression : Boolean = false) {
        Thread {
            val url = URL(urlName)
            val urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "POST"
            urlConnection.setRequestProperty("content-type", requestType)
            if (compression) {
                urlConnection.setRequestProperty("X-Network", "CSD")
                urlConnection.setRequestProperty("X-Content-Encoding", "deflate")
            }

            try {
                var os :OutputStream
                if (compression) {
                    os = DeflaterOutputStream(urlConnection.outputStream, Deflater(Deflater.DEFAULT_COMPRESSION, true))
                } else {
                    os = urlConnection.outputStream
                }
                val output = OutputStreamWriter(os)
                output.write(request)
                output.flush()
                output.close()

                var inputStream : InputStream
                if (compression) {
                    inputStream = InflaterInputStream(urlConnection.inputStream, Inflater(true))
                } else {
                    inputStream = urlConnection.inputStream
                }
                val input = InputStreamReader(inputStream)
                var text = input.readText()
                val handler = android.os.Handler(Looper.getMainLooper())
                handler.post {
                    l.handleServerResponse(text)
                }
            } catch (e : Exception) {
                e.printStackTrace()
            } finally {
                urlConnection.disconnect()
            }
        }.start()
    }


}