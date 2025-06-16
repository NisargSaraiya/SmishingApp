package com.example.smishingapp.ui

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.provider.Telephony
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smishingapp.network.Api
import com.example.smishingapp.network.UserInput
import com.example.smishingapp.network.UserModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.IOException
import java.lang.Long.max
import java.util.*


val dateComparator = Comparator<Message> { m1, m2 ->
    m2.date.compareTo(m1.date)
}
class MessageViewModel:ViewModel() {

    private val _uiState = MutableStateFlow(MessageUiState())
    val uiState: StateFlow<MessageUiState> = _uiState.asStateFlow()

    private val map: HashMap<String, MutableList<Message> > = hashMapOf()
    private val blockList: MutableSet<String> = mutableSetOf()
    private var currentDate:Long = 0

    private fun addElement(myHashMap:HashMap<String,MutableList<Message> >, myKey:String, newMessage: Message){
        if (myHashMap.containsKey(myKey)) {

            val myList = myHashMap[myKey]
            myList?.add(newMessage)
            myHashMap[myKey] = myList!!
        } else {
            // If the key doesn't exist, create a new MutableList and add the new element
            val myList = mutableListOf(newMessage)
            myHashMap[myKey] = myList
            _uiState.value.allMessage.add(newMessage)
            _uiState.value.ham.add(newMessage)
        }
    }

    private fun getScore(message:Message,value:Message){
        viewModelScope.launch {
            try {
                val result: UserModel = Api.retrofitService.getScore(UserInput(message = message.text))
                val ans = result.score[0][0]
                if (ans >= 0.5f) {
                    message.isSpam = true
                    blockList.add(message.sender)
                    _uiState.value.spam.add(value)
                    if(_uiState.value.ham.contains(value))
                        _uiState.value.ham.remove(value)
                    Log.d("added", result.toString() + blockList.size)
                }
                Log.e("Main", "Success$ans")
            } catch (e: IOException) {
                Log.e("Main", e.message.toString())
            }
        }
    }
    @SuppressLint("Recycle")
    fun getAllMessage(context: Context){

        viewModelScope.launch {
            val contentResolver: ContentResolver = context.contentResolver
            val projection = arrayOf(
                Telephony.Sms.BODY,
                Telephony.Sms.ADDRESS,
                Telephony.Sms.TYPE,
                Telephony.Sms.DATE
            )
            val selection = "DATE > $currentDate"

            val cursor = contentResolver.query(
                Telephony.Sms.CONTENT_URI,
                projection, selection, null, null
            )
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val date = cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE))
                    val address: String? =
                        cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))
                    val type = cursor.getInt(cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE))
                    val message = Message(
                        text = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)),
                        sender = address ?: "",
                        date = date,
                        type = when (type) {
                            Telephony.Sms.MESSAGE_TYPE_INBOX -> "inbox"
                            else -> "outbox"
                        }
//                        name = getContentNameFromNumber(address?:"",contentResolver)
                    )
                    currentDate = max(date,currentDate)
                    addElement(map, address ?: "", message)
                    if (!blockList.contains(message.sender) && (hasUrl(message.text) || hasEmailOrPhone(message.text))) {
                        map[message.sender]?.let { getScore(message, it.first()) }
                    }
                    if (map.size > 20)
                        break
                }
            }
            delay(25000)
            getAllMessage(context)
        }
    }

    private fun getContentNameFromNumber(number: String, contentResolver: ContentResolver): String {
        val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number))
        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        val name = if (cursor?.moveToFirst() == true) {
            val index = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)
            cursor.getString(index)
        } else {
            ""
        }
        cursor?.close()
        return name
    }

    private fun hasUrl(text:String):Boolean{
        val pattern = Regex("(http|https)?://(www\\.)?\\S+")
        return pattern.containsMatchIn(text)
    }
    private fun hasEmailOrPhone(text:String):Boolean{
        val pattern = Regex("\\b[A-Za-z\\d._%+-]+@[A-Za-z\\d.-]+\\.[A-Z|a-z]{2,}\\b")
        val patternMobile = Regex("\\(?(\\d{3})\\)?[-.\\s]?(\\d{3})[-.\\s]?(\\d{4})")
        return (pattern.containsMatchIn(text) || patternMobile.containsMatchIn(text))
    }


    fun updateAllUiState(message: Message){
        _uiState.update{currentState ->
            currentState.copy(
                allMessage = (_uiState.value.allMessage + message) as SortedSet<Message>
            )
        }
    }

    fun updateSpamUiState(message: Message){
        _uiState.update{currentState ->
            currentState.copy(
                spam = (_uiState.value.allMessage + message) as SortedSet<Message>
            )
        }
    }

    fun updateHamUiState(message: Message){
        _uiState.update{currentState ->
            currentState.copy(
                ham = (_uiState.value.allMessage + message) as SortedSet<Message>
            )
        }
    }
    fun updateConversation(address: String){
        val messageList = map[address]!!
        _uiState.update{currentState ->
            currentState.copy(
                chat = messageList
            )
        }
    }

    fun updateChange(message: Message)
    {
        if(message.isSpam)
        {
            _uiState.value.spam.remove(message)
            message.isSpam = false
            _uiState.value.ham.add(message)
        }
        else{
            _uiState.value.ham.remove(message)
            message.isSpam = true
            _uiState.value.spam.add(message)
        }
    }
}
//    lateinit var classifier:Classifier
//
//    lateinit var model:File
//    lateinit var interpreter:Interpreter

//    private fun classify(message:Message):Float{
//        val textLower = message.text.lowercase().trim()
//        val tokenizedMessage = classifier.tokenize(textLower)
//        val paddedMessage = classifier.padSequence(tokenizedMessage)
//
////       val inputs : Array<FloatArray> = arrayOf( paddedMessage.map{ it.toFloat() }.toFloatArray() )
////        val outputs : Array<FloatArray> = arrayOf(floatArrayOf(0.0f))
//
//        Log.d("message",paddedMessage.size.toString())
//        val inputBuffer = ByteBuffer.allocateDirect(paddedMessage.size * 4).apply {
//            for (token in paddedMessage) {
//                putFloat(token.toFloat())
//            }
//            rewind()
//        }
//        val outputBuffer = Array(1) { FloatArray(1) }
//
//        interpreter.run( inputBuffer,outputBuffer)
//        return outputBuffer[0][0]
////        return classifier.modelClassification(paddedMessage)[0]
//    }
