package com.example.labo2

class Person (
    val name: String,
    val firstName: String,
    val gender: String,
    val phone: String
        ){
    override fun toString(): String {
        return name + ", " + firstName + ", " + gender + ", " + phone
    }
}