package com.example.labo2.graphql

data class Author(val id : Int, val name : String) {
    override fun toString() : String {
        return name
    }
}
