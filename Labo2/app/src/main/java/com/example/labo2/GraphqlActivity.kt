package com.example.labo2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import org.json.JSONObject

class GraphqlActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    lateinit var spinner: Spinner
    lateinit var list : ListView
    val endpoint =  "http://mobile.iict.ch/graphql"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graphql)

        spinner = findViewById(R.id.graphql_spinner)
        list    = findViewById(R.id.graphql_list)

        var authorAdapter = ArrayAdapter<Author>(this, android.R.layout.simple_list_item_1)

        val requestAuthors = "{\"query\": \"{findAllAuthors{id, name}}\"}"
        val Authorscm = SymComManager( object : CommunicationEventListener{
            override fun handleServerResponse(response: String) {
                val authorArray = JSONObject(response).getJSONObject("data").getJSONArray("findAllAuthors")
                for(i : Int in 0..(authorArray.length() - 1) ) {
                    val authorObject = authorArray.getJSONObject(i)
                    var authorName = authorObject.getString("name")
                    var authorId = authorObject.getInt("id")
                    authorAdapter.add(Author(authorId,authorName))
                }
            }
        })
        Authorscm.sendRequest(requestAuthors, endpoint, "application/json")

        authorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = authorAdapter

        spinner.onItemSelectedListener = this
    }

    override fun onItemSelected(parent: AdapterView<*>, view : View?, pos: Int, id: Long) {
        var author = parent.getItemAtPosition(pos)
        if (author is Author) {
            val requestBooksByAuthor = "{\"query\": \"{findAuthorById(id:${author.id}){books{id, title, languageCode, authors{name}}}}\"}"
            val bookScm = SymComManager( object : CommunicationEventListener{
                override fun handleServerResponse(response: String) {
                    println(response)

                    val bookAdapter = ArrayAdapter<String>(this@GraphqlActivity, android.R.layout.simple_expandable_list_item_1)
                    val bookArray = JSONObject(response).getJSONObject("data").getJSONObject("findAuthorById").getJSONArray("books")
                    for (i : Int in 0..(bookArray.length() - 1)) {
                        val bookObject = bookArray.getJSONObject(i)
                        var bookTitle = bookObject.getString("title")
                        bookAdapter.add(bookTitle)
                    }
                    list.adapter = bookAdapter
                }
            })
            bookScm.sendRequest(requestBooksByAuthor, endpoint, "application/json")
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }


}