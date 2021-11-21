package com.example.labo2.graphql

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import com.example.labo2.comm.CommunicationEventListener
import com.example.labo2.R
import com.example.labo2.comm.SymComManager
import org.json.JSONObject

class GraphqlActivity : AppCompatActivity() {

    lateinit var spinner: Spinner
    lateinit var list : ListView
    var spinnerLastSelection = -1
    val endpoint =  "http://mobile.iict.ch/graphql"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graphql)


        spinner = findViewById(R.id.graphql_spinner)
        list    = findViewById(R.id.graphql_list)

        var authorAdapter = ArrayAdapter<Author>(this, android.R.layout.simple_list_item_1)

        val requestAuthors = "{\"query\": \"{findAllAuthors{id, name}}\"}"
        val Authorscm = SymComManager( object : CommunicationEventListener {
            override fun handleServerResponse(response: ByteArray) {
                val authorArray = JSONObject(String(response)).getJSONObject("data").getJSONArray("findAllAuthors")
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

        spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view : View?, pos: Int, id: Long) {
                if (spinnerLastSelection == pos) return
                spinnerLastSelection = pos
                var author = parent.getItemAtPosition(pos)
                if (author is Author) {
                    val requestBooksByAuthor = "{\"query\": \"{findAuthorById(id:${author.id}){books{title}}}\"}"
                    val bookScm = SymComManager( object : CommunicationEventListener {
                        override fun handleServerResponse(response: ByteArray) {
                            val bookAdapter = ArrayAdapter<String>(this@GraphqlActivity, android.R.layout.simple_expandable_list_item_1)
                            val bookArray = JSONObject(String(response)).getJSONObject("data").getJSONObject("findAuthorById").getJSONArray("books")
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
        })
    }
}