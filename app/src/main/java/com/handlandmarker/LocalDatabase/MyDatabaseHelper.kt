package com.handlandmarker.LocalDatabase

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import android.os.Message
import android.provider.MediaStore.Audio.Genres.Members
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.Group
import com.handlandmarker.accets.My_Group
import com.handlandmarker.accets.Users
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date


fun getCurrentTimeAsString(date: LocalDateTime): String {
    val currentTime = date
    val formatter = DateTimeFormatter.ofPattern("yyyy-DDD HH:mm:ss") // Corrected time format
    return formatter.format(currentTime)
}

fun parseTimeString(timeString: String): LocalDateTime {
    val formatter = DateTimeFormatter.ofPattern("yyyy-DDD HH:mm:ss") // Corrected time format
    return LocalDateTime.parse(timeString, formatter)
}

class MyDatabaseHelper(var context: Context,var UID: String) {
    private val DATABASE_NAME = UID
    private val DATABASE_VERSION = 1
    // -------------Group_Table------------------------------
    private val Group_Table = "_Group"
    private val Group_ID = "_id"
    private val Group_Name = "_name"
    private val Group_Admin = "_Admins_Key"
    private val Group_Members = "_Members_Key"
    private val Group_Privacy_Add = "_Privacy_Add_Members"
    private val Group_Privacy_Message = "_Privacy_Message"
    //-----------------------Member_Table------------------------------
    private val Members_Table = "_Members"
    private val Members_Name = "_Name"
    private val Members_ID = "_UID"
    //-----------------------Message_Table---------------------------------
    private val Message_Table = "_Message"
    private val Message_ID = "_ID"
    private val Message_Text ="_Text"
    private val Message_Sender = "_Sender_ID"
    private val Message_Group = "Message_Group_ID"
    private val Message_Time = "Time_Sent"


    //==================helper_Class--------------------------------
    public var  helper: CreateDataBase? = null
    var database: SQLiteDatabase? = null

    fun AddMember(ID:String,MemberID:String,Name:String)
    {
        var cv = ContentValues()
        val members = getGroupMembers(ID)
        if(members != null) {
            cv.put(Group_Members, members + "," + MemberID)
        }else
        {
            cv.put(Group_Members,Members_ID)
        }
        var cv2 = ContentValues()
        cv2.put(Members_ID,MemberID)
        cv.put(Members_Name,Name)
        database?.insert(Members_Table,null,cv2)
        val rec = database?.update(Group_Table,cv,"$Group_ID =?", arrayOf(ID))
    }

    fun getGroupMembers(ID: String): String? {
        val members = ArrayList<String>()
        val db = database
        val selectQuery = "SELECT $Group_Members FROM $Group_Table WHERE $Group_ID = $ID"

        var cursor:Cursor? = db?.rawQuery(selectQuery, null)
        cursor?.moveToFirst()
        val Index = cursor?.getColumnIndex(Group_Members)
        val members1 = Index?.let { cursor?.getString(it) }
        cursor?.close()
        return members1
    }

    fun AddMessage(Group_id:String, MemberID: String, message: String, Time_stamp: LocalDateTime)
    {
        val cv = ContentValues()
        cv.put(Message_Group,Group_id)
        cv.put(Message_Sender,MemberID)
        cv.put(Message_Text,message)
        cv.put(Message_Time, getCurrentTimeAsString(Time_stamp))
        database?.insert(Message_Table,null,cv)
    }

    fun ChangeMemberAddPrivacy(Group_id: String,privacy: Int)
    {
        // 1 for anyone can join , 2 only with code , 3 admin can add only
        val cv = ContentValues()
        cv.put(Group_Privacy_Add,privacy)
        database?.update(Group_Table,cv,"$Group_ID =?", arrayOf(Group_id))
    }
    fun ChangeMemberTextPrivacy(Group_id: String,privacy: Int)
    {
        // 1 for anyone can Message , 2 only Admin can message
        val cv = ContentValues()
        cv.put(Group_Privacy_Message,privacy)
        database?.update(Group_Table,cv,"$Group_ID =?", arrayOf(Group_id))
    }

    fun GetALLUsers(): ArrayList<Users>
    {

        val selectQuery = "SELECT * FROM $Members_Table"
        var arr: ArrayList<Users> = ArrayList()
        val cursor: Cursor? = database?.rawQuery(selectQuery, null)
       if(cursor?.moveToFirst() == true)
       {
           val nameIndex = cursor?.getColumnIndex(Members_Name)
           val IDIndex = cursor?.getColumnIndex(Members_ID)
        do {
            if (IDIndex != null && nameIndex != null) {
                arr.add(Users(cursor.getString(nameIndex),cursor.getString(IDIndex)))
            }
        }while(cursor?.moveToNext()== true)
       }
        cursor?.close()
        return arr
    }


    fun GetAllGroups(array: ArrayList<Users>): ArrayList<My_Group>
    {
        val selectQuery = "SELECT * FROM $Group_Table"


        var arr: ArrayList<My_Group> = ArrayList()
        val cursor: Cursor? = database?.rawQuery(selectQuery, null)
        if(cursor?.moveToFirst() == true)
        {
            val Group_id_Index = cursor.getColumnIndex(Group_ID)
            val nameIndex = cursor.getColumnIndex(Group_Name)
            val Members_Ind = cursor.getColumnIndex(Group_Members)
            val Admin_ind = cursor.getColumnIndex(Group_Admin)
            val Add_Priv_ind = cursor.getColumnIndex(Group_Privacy_Add)
            val message_Priv_ind = cursor.getColumnIndex(Group_Privacy_Message)
            do {
                if (Group_id_Index != null && nameIndex != null) {
                    var Members = cursor.getString(Members_Ind)
                    var arrM = GetMembers(Members)
                    var admin = cursor.getString(Admin_ind)
                    var arrA = GetMembers(admin)
                //    arr.add(My_Group(cursor.getString(Group_id_Index),cursor.getString(nameIndex),arrM,arrA,
                  //      cursor.getInt(Add_Priv_ind),cursor.getInt(Admin_ind)),)
                }
            }while(cursor.moveToNext() == true)
        }
        cursor?.close()
        return arr
    }

    fun GetMembers(str:String):ArrayList<String>
    {
        return ArrayList(str.split(","))
    }
    fun GetLastMessage(Group_id: String): com.handlandmarker.accets.Message?
    {
        val db = database
        val selectQuery = "SELECT * FROM $Message_Table WHERE $Message_Group = $Group_id ORDER BY $Message_Time DESC LIMIT 1"

        val cursor: Cursor? = db?.rawQuery(selectQuery, null)
        if(cursor?.moveToFirst() == true) {
            val Message_ID1 = cursor?.getColumnIndex(Message_ID)
            val Message_Text1 = cursor?.getColumnIndex(Message_Text)
            val Message_Sender1 = cursor?.getColumnIndex(Message_Sender)
            val Message_Group1 = cursor?.getColumnIndex(Message_Group)
            val Message_Time1 = cursor?.getColumnIndex(Message_Time)
            if(Message_Group1!= null && Message_Sender1!= null && Message_Text1!= null && Message_Time1!= null && Message_ID1 != null ) {
                val lastestMessage = com.handlandmarker.accets.Message(cursor.getInt(Message_ID1),
                    cursor.getString(Message_Text1),
                    cursor.getString(Message_Sender1),
                    cursor.getString(Message_Group1),
                    parseTimeString(cursor.getString(Message_Time1))
                )
                cursor.close()
                return lastestMessage
            }
        }
        cursor?.close()
        return null
    }
    fun updateMessage(id: Int, newMessage: String?) {
        val cv = ContentValues()
        cv.put(Message_Text, newMessage)
        val records = database!!.update(Group_Table, cv, "$Message_ID=?", arrayOf(id.toString() + ""))
        if (records > 0) {
            Toast.makeText(context, "Contact updated", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Contact not updated", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteMessage(id: Int) {
        val rows = database!!.delete(Message_Table, "$Message_ID=?", arrayOf(id.toString() + ""))
        if (rows > 0) {
            Toast.makeText(context, "Contact deleted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Contact not deleted", Toast.LENGTH_SHORT).show()
        }
    }

    fun insertGroup(Id: String,Group_name: String,MemberID: String,AdminID: String) {
        val cv = ContentValues()
        cv.put(Group_ID,Id)
        cv.put(Group_Name,Group_name)
        cv.put(Group_Members,MemberID)
        cv.put(Group_Admin,AdminID)
        cv.put(Group_Privacy_Message,1)
        cv.put(Group_Privacy_Add,1)
        val records = database!!.insert(Group_Table, null, cv)
        if (records == -1L) {
            Toast.makeText(context, "Data not inserted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Total $records contacts added", Toast.LENGTH_SHORT).show()
        }
    }

    fun readAllMessage(Group_id: String): ArrayList<com.handlandmarker.accets.Message> {
        val records: ArrayList<com.handlandmarker.accets.Message> = ArrayList()
        val cursor = database!!.rawQuery("SELECT * FROM $Message_Table WHERE $Message_Group = $Group_id", null)
        val id_Index = cursor.getColumnIndex(Message_ID)
        val MessageIndex = cursor.getColumnIndex(Message_Text)
        val dateIndex = cursor.getColumnIndex(Message_Time)
        val senderInd= cursor.getColumnIndex(Message_Sender)
        if (cursor.moveToFirst()) {
            do {
                val c = com.handlandmarker.accets.Message(
                cursor.getInt(id_Index),
                cursor.getString(MessageIndex),
                cursor.getString(senderInd),
                Group_id,
                    parseTimeString( cursor.getString(dateIndex)))
                records.add(c)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return records
    }

    fun open() {
        helper = CreateDataBase(context, DATABASE_NAME, null, DATABASE_VERSION)
        database = helper!!.writableDatabase
    }

    fun close() {
        database!!.close()
        helper!!.close()
    }

    inner class CreateDataBase(
        context: Context?,
        name: String?,
        factory: CursorFactory?,
        version: Int
    ) :
        SQLiteOpenHelper(context, name, factory, version) {
        override fun onCreate(db: SQLiteDatabase) {
            val createGroupTableQuery = """
    CREATE TABLE $Group_Table (
        $Group_ID TEXT PRIMARY KEY,
        $Group_Name TEXT,
        $Group_Admin TEXT,
        $Group_Members TEXT,
        $Group_Privacy_Add INTEGER,
        $Group_Privacy_Message INTEGER
    )
"""

            val createMembersTableQuery = """
    CREATE TABLE $Members_Table (
        $Members_Name TEXT,
        $Members_ID TEXT PRIMARY KEY
    )
"""

            val createMessageTableQuery = """
    CREATE TABLE $Message_Table (
        $Message_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $Message_Text TEXT,
        $Message_Sender TEXT,
        $Message_Group TEXT,
        $Message_Time TEXT
    )
"""
            db.execSQL(createGroupTableQuery)
            db.execSQL(createMessageTableQuery)
            db.execSQL(createMembersTableQuery)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS $Group_Table")
            db.execSQL("DROP TABLE IF EXISTS $Members_Table")
            db.execSQL("DROP TABLE IF EXISTS $Message_Table")
            onCreate(db)
        }


    }
}