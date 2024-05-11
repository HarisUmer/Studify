package com.handlandmarker.accets

class Users(private var Members_Name: String?,
            private val Members_ID: String,
    ) {
    lateinit var Groups_Member: ArrayList<String>
    fun getUserID(): String {
        return Members_ID
    }

    fun getUserName(): String? {
        return Members_Name
    }

    fun setGroups(p1: ArrayList<String>)
    {
        Groups_Member = p1
    }
    fun setUserName(nam: String)
    {
        Members_Name = nam
    }
    fun getGroups():ArrayList<String>
    {

        return Groups_Member
    }
}