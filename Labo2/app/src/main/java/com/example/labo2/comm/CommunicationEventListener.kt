package com.example.labo2.comm

interface CommunicationEventListener {
    fun handleServerResponse(response : ByteArray)
}