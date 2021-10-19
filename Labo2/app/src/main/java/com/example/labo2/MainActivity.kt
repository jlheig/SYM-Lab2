package com.example.labo2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button


class MainActivity : AppCompatActivity() {

    private lateinit var asyncBtn :Button
    private lateinit var differBtn :Button
    private lateinit var objectBtn :Button
    private lateinit var graphqlBtn :Button
    private lateinit var compressBtn :Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        asyncBtn    = findViewById(R.id.main_asyncButton)
        differBtn   = findViewById(R.id.main_differButton)
        objectBtn   = findViewById(R.id.main_objectButton)
        graphqlBtn  = findViewById(R.id.main_graphqlButton)
        compressBtn = findViewById(R.id.main_compressButton)
    }

    fun goAsyncActivity(view : View) {
        val intent = Intent(this, AsyncActivity::class.java)
        startActivity(intent)
    }
}

