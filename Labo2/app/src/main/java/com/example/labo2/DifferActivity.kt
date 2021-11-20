package com.example.labo2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class DifferActivity : AppCompatActivity() {

    lateinit var requestText : EditText
    lateinit var sendButton : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_differ)

        requestText = findViewById(R.id.differ_sendContent)
        sendButton = findViewById(R.id.differ_sendButton)

        sendButton.setOnClickListener {
            val mQ = MessageQueue()
            mQ.sendRequest( requestText.getText().toString(), "http://mobile.iict.ch/api/txt" )
        }
    }
}