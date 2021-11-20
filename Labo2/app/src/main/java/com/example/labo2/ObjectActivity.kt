package com.example.labo2

import android.os.Bundle
import android.provider.MediaStore
import android.util.Xml
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.labo2.protobuf.DirectoryOuterClass
import com.google.gson.Gson
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.xml.sax.InputSource
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader
import java.io.StringWriter
import java.lang.StringBuilder
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

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
                    dsResponse = "test"
                    //dsResponse = deserializePB(response).toString()
                }

                textReceived.setText(dsResponse)
            }
        })



        sendButton.setOnClickListener{
            var data: String
            val person = Person("Blanc", "Jean-Luc", "homme", "0798403448")
            if(radioGroup.checkedRadioButtonId == R.id.radio_json) {
                data = serializeJSON(person)
                syComManager.sendRequest(data, "http://mobile.iict.ch/api/json")
            }
            else if (radioGroup.checkedRadioButtonId == R.id.radio_xml) {
                data = serializeXML(person)
                syComManager.sendRequest(data, "http://mobile.iict.ch/api/xml")
            }
            else {
                //data = serializePB(person)
                //syComManager.sendRequest(data, "http://mobile.iict.ch/api/protobuf")
            }
        }
    }

    fun serializeXML(person: Person): String {
        val docBuild: DocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc: Document = docBuild.newDocument()

        val rootElem: Element = doc.createElement("person")
        val name: Element = doc.createElement("name")
        name.appendChild(doc.createTextNode(person.name))
        rootElem.appendChild(name)
        val firstName: Element = doc.createElement("firstName")
        firstName.appendChild(doc.createTextNode(person.firstName))
        rootElem.appendChild(firstName)
        val gender: Element = doc.createElement("gender")
        gender.appendChild(doc.createTextNode(person.gender))
        rootElem.appendChild(gender)
        val phone: Element = doc.createElement("phone")
        phone.appendChild(doc.createTextNode(person.phone))
        rootElem.appendChild(phone)

        val transformer: Transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "no")
        transformer.setOutputProperty(OutputKeys.METHOD, "xml")
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8")
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http:mobile.iict.ch/directory.dtd")

        val source = DOMSource(doc)
        val writer = StringWriter()
        val result = StreamResult(writer)
        transformer.transform(source, result)

        return writer.toString().replace("\n","")
    }

    fun deserializeXML(person: String): Person {
        val docBuild = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val input = InputSource()
        input.setCharacterStream(StringReader(person))
        val doc = docBuild.parse(input)
        val name = doc.getElementsByTagName("name").item(0).textContent
        val firstName = doc.getElementsByTagName("firstName").item(0).textContent
        val gender = doc.getElementsByTagName("gender").item(0).textContent
        val phone = doc.getElementsByTagName("phone").item(0).textContent

        return Person(name, firstName, gender, phone)
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
                .setFirstname(person.firstName)
                .setGender(person.gender)
                .setPhone(person.phone)
        val pbPerson = pPerson.build()

        dir.addResults(pbPerson)
        return dir.build().toByteArray()
    }

    fun deserializePB(person: ByteArray): Person {
        val dir = DirectoryOuterClass.Directory.parseFrom(person)
        val person = dir.resultsList[0]
        return Person(person.name, person.firstname, person.gender, person.phone)
    }

}
