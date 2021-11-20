package com.example.labo2

import com.example.labo2.protobuf.DirectoryOuterClass
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText
import java.lang.Exception

class Phone(
    @JacksonXmlText(value = true)
    val number: String,
    @JacksonXmlProperty(isAttribute = true)
    val type : Type
    ){

    enum class Type(
            val type: String
    ){
        @JsonProperty("home")
        HOME("home"),
        @JsonProperty("mobile")
        MOBILE("mobile"),
        @JsonProperty("work")
        WORK("work");

        override fun toString(): String {
            return type
        }

        companion object {
            fun getValue(type: String?): Type = values().find {
                it.type == type
            } ?: Type.HOME
        }
    }

    override fun toString(): String {
        return "Phone: " + type + " " + number
    }

    fun directoryPhoneType(type: Phone.Type): DirectoryOuterClass.Phone.Type? {
        return when (type) {
            Type.HOME -> DirectoryOuterClass.Phone.Type.HOME
            Type.MOBILE -> DirectoryOuterClass.Phone.Type.MOBILE
            Type.WORK -> DirectoryOuterClass.Phone.Type.WORK
        }
    }

    companion object {
        fun PhoneType(type: DirectoryOuterClass.Phone.Type): Type {
            return when (type) {
                DirectoryOuterClass.Phone.Type.HOME -> Type.HOME
                DirectoryOuterClass.Phone.Type.WORK -> Type.WORK
                DirectoryOuterClass.Phone.Type.MOBILE -> Type.MOBILE
                DirectoryOuterClass.Phone.Type.UNRECOGNIZED -> throw Exception("Unknown phone type")
            }
        }
    }


}

@JacksonXmlRootElement(localName = "person")
class Person (
    val name: String,
    val firstname: String,
    val middlename: String,
    @JacksonXmlElementWrapper(localName = "phone", useWrapping = false)
    val phone: List<Phone>
        ){
    override fun toString(): String {
        val s = StringBuilder()
        s.append(name + ", " + firstname + ", " + middlename)
        for (p in phone){
            s.append(", " + p.toString())
        }
        return s.toString()
    }
}