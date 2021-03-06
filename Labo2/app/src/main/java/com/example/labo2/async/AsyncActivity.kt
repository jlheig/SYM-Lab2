package com.example.labo2.async

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.labo2.comm.CommunicationEventListener
import com.example.labo2.R
import com.example.labo2.comm.SymComManager

class AsyncActivity : AppCompatActivity() {

    lateinit var sendButton : Button
    lateinit var requestText : EditText
    lateinit var receptionText : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_async)

        requestText = findViewById(R.id.async_sendContent)
        receptionText = findViewById(R.id.async_receptionContent)
        sendButton = findViewById(R.id.async_sendButton)

        sendButton.setOnClickListener {
            val scm = SymComManager( object : CommunicationEventListener {
                override fun handleServerResponse(response : ByteArray) {
                    receptionText.setText(String(response))
                }
            })

            scm.sendRequest( requestText.getText().toString(), "http://mobile.iict.ch/api/txt", "text/plain" )
        }
    }
}