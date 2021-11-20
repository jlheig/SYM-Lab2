package com.example.labo2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.labo2.async.AsyncActivity
import com.example.labo2.differ.DifferActivity
import com.example.labo2.graphql.GraphqlActivity


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

        asyncBtn.setOnClickListener {
            val intent = Intent(this, AsyncActivity::class.java)
            startActivity(intent)
        }

        graphqlBtn.setOnClickListener{
            val intent = Intent(this, GraphqlActivity::class.java)
            startActivity(intent)
        }

        differBtn.setOnClickListener {
            val intent = Intent(this, DifferActivity::class.java)
            startActivity(intent)
        }
    }

}

