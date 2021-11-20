package com.example.labo2.differ

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.labo2.R

class DifferActivity : AppCompatActivity() {

    lateinit var requestText : EditText
    lateinit var sendButton : Button
    private val mQ = MessageQueue()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_differ)

        requestText = findViewById(R.id.differ_sendContent)
        sendButton = findViewById(R.id.differ_sendButton)

        sendButton.setOnClickListener {
            mQ.sendRequest( requestText.getText().toString(), "http://mobile.iict.ch/api/txt")
        }
    }
}