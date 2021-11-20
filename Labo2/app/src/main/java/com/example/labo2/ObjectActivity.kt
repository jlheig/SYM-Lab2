package com.example.labo2

import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.labo2.comm.CommunicationEventListener
import com.example.labo2.comm.SymComManager
import com.example.labo2.protobuf.DirectoryOuterClass
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.gson.Gson
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

class ObjectActivity : AppCompatActivity() {

    private lateinit var sendButton: Button
    private lateinit var textReceived: TextView
    private lateinit var radioGroup : RadioGroup
    protected lateinit var syComManager: SymComManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setting up the UI
        setContentView(R.layout.activity_object)

        sendButton = findViewById(R.id.object_sendButton)
        radioGroup = findViewById(R.id.object_radio_group)
        textReceived = findViewById(R.id.object_receptionTitle)


        syComManager = SymComManager( object : CommunicationEventListener {
            override fun handleServerResponse(response : String) {
                val dsResponse: String
                if(radioGroup.checkedRadioButtonId == R.id.radio_json) {
                    dsResponse = deserializeJSON(response).toString()
                }
                else if (radioGroup.checkedRadioButtonId == R.id.radio_xml) {
                    dsResponse = deserializeXML(response).toString()
                }
                else {
                    dsResponse = deserializePB(response.toByteArray()).toString()
                }

                textReceived.setText(dsResponse)
            }
        })



        sendButton.setOnClickListener{
            var data: String
            var phone1: Phone = Phone("0123456789", Phone.Type.HOME)
            var phone2: Phone = Phone("0123456789", Phone.Type.MOBILE)
            var phones: List<Phone> = listOf(phone1, phone2)
            if(radioGroup.checkedRadioButtonId == R.id.radio_json) {
                val person = Person("JSON", "Test", "serialize", phones)
                data = serializeJSON(person)
                syComManager.sendRequest(data, "http://mobile.iict.ch/api/json", "application/json", false)
            }
            else if (radioGroup.checkedRadioButtonId == R.id.radio_xml) {
                val person = Person("XML", "Test", "serialize", phones)
                data = serializeXML(person)
                syComManager.sendRequest(data, "http://mobile.iict.ch/api/xml", "application/xml", false)
            }
            else {
                val person = Person("ProtoBuf", "Test", "serialize", phones)
                val pbData = serializePB(person)
                syComManager.sendRequestByte(pbData, "http://mobile.iict.ch/api/protobuf", "application/protobuf", false)
            }
        }
    }

    fun serializeXML(person: Person): String {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<!DOCTYPE directory SYSTEM \"http://mobile.iict.ch/directory.dtd\">" +
                "<directory>" +
                XmlMapper().registerKotlinModule().writeValueAsString(person) +
                "</directory>"
    }

    fun deserializeXML(person: String): Person {
        val docBuild = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val input = InputSource()
        input.setCharacterStream(StringReader(person))
        val doc = docBuild.parse(input)
        val name = doc.getElementsByTagName("name").item(0).textContent
        val firstname = doc.getElementsByTagName("firstname").item(0).textContent
        val middlename = doc.getElementsByTagName("middlename").item(0).textContent
        val phone = doc.getElementsByTagName("phone")

        val phones: MutableList<Phone> = mutableListOf()

        for(i in 0..(phone.length-1)){
            phones.add(Phone(phone.item(i).textContent, Phone.Type.getValue(phone.item(i).attributes.item(0).textContent)))
        }

        return Person(name, firstname, middlename, phones)
    }

    fun serializeJSON(person: Person): String {
        return Gson().toJson(person)
    }

    fun deserializeJSON(person: String): Person {
        return Gson().fromJson(person, Person::class.java)
    }

    fun serializePB(person: Person): ByteArray {
        val dir = DirectoryOuterClass.Directory.newBuilder()

        val pPerson = DirectoryOuterClass.Person.newBuilder()
                .setName(person.name)
                .setFirstname(person.firstname)
                .setMiddlename(person.middlename)

        for(p in person.phone){
            val pPhone = DirectoryOuterClass.Phone.newBuilder()
                .setType(p.directoryPhoneType(p.type))
                .setNumber(p.number)
                .build()
            pPerson.addPhone(pPhone)
        }

        val pbPerson = pPerson.build()

        dir.addResults(pbPerson)
        return dir.build().toByteArray()
    }

    fun deserializePB(person: ByteArray): Person {
        val dir = DirectoryOuterClass.Directory.parseFrom(person)
        val person = dir.resultsList[0]
        val phones: MutableList<Phone> = mutableListOf()

        for(p in person.phoneList){
            phones.add(Phone(p.number, Phone.PhoneType(p.type)))
        }

        return Person(person.name, person.firstname, person.middlename, phones)
    }

}
