package com.example.labo2.compression

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.labo2.R
import com.example.labo2.comm.CommunicationEventListener
import com.example.labo2.comm.SymComManager

lateinit var sendButton : Button
lateinit var requestText : EditText
lateinit var receptionText : EditText

class CompressionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compression)

        requestText = findViewById(R.id.compression_sendContent)
        receptionText = findViewById(R.id.compression_receptionContent)
        sendButton = findViewById(R.id.compression_sendButton)

        sendButton.setOnClickListener {
            val scm = SymComManager( object : CommunicationEventListener {
                override fun handleServerResponse(response : String) {
                    receptionText.setText(response)
                }
            })

            scm.sendRequest( requestText.getText().toString(), "http://mobile.iict.ch/api/txt", "text/plain", true )
        }
    }
}