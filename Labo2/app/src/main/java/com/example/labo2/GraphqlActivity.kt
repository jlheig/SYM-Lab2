package com.example.labo2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner

class GraphqlActivity : AppCompatActivity() {

    /* requete :
    {
        "query": "{findAllAuthors{id, name}}"
    }
    */

    lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graphql)

        spinner = findViewById(R.id.graphql_spinner)

        val endpoint = "http://mobile.iict.ch/graphql"
        val requestAuthors = "{\"query\": \"{findAllAuthors{id, name}}\"}"
        val scm = SymComManager( object : CommunicationEventListener{
            override fun handleServerResponse(response: String) {
                println(response) // TODO: stocke data in spinner
            }
        })
        scm.sendRequest(requestAuthors, endpoint, "application/json")

        ArrayAdapter.createFromResource(this, R.array.test, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }
    }
}