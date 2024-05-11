package com.handlandmarker.accets

import com.handlandmarker.LocalDatabase.getCurrentTimeAsString
import java.time.LocalDateTime
import java.util.Date

class Message(
    private  val ID: Int,
    private val Message_Text: String,
    private val Message_Sender: String,
    private val Message_Group: String,
    private val Message_Time: LocalDateTime,)
{

fun getSender(): String
{
    return Message_Sender
}
    fun  getText():String
    {
        return  Message_Text
    }
    fun getTime():String
    {
        return  getCurrentTimeAsString(Message_Time)
    }

}