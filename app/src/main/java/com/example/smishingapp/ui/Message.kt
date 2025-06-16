package com.example.smishingapp.ui


data class Message(val text: String, val sender:String = "", val type: String, val date: Long,
                   var isSpam: Boolean = false,var name: String = "")
