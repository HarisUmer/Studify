package com.handlandmarker.accets

class My_Group(
    private var Group_ID: String,
    private var Group_Name: String,
    private var Group_Members: ArrayList<String>,
    private var Admin: ArrayList<String>,
    private var Group_Privacy_Add: Int,
    private var Group_Privacy_Message: Int,
    private var Message_Count: Int
) {
    // Getter and setter methods for Group_ID
    fun getGroupID(): String {
        return Group_ID
    }
    fun getCount(): Int{
        return Message_Count
    }
    fun setCount(i: Int )
    {
        Message_Count = i
    }
    fun setGroupID(groupID: String) {
        Group_ID = groupID
    }

    // Getter and setter methods for Group_Name
    fun getGroupName(): String {
        return Group_Name
    }

    fun setGroupName(groupName: String) {
        Group_Name = groupName
    }

    // Getter and setter methods for Group_Members
    fun getGroupMembers(): ArrayList<String> {
        return Group_Members
    }

    fun setGroupMembers(groupMembers: ArrayList<String>) {
        Group_Members = groupMembers
    }

    // Getter and setter methods for Admin
    fun getAdmin(): ArrayList<String> {
        return Admin
    }

    fun setAdmin(admin: ArrayList<String>) {
        Admin = admin
    }

    // Getter and setter methods for Group_Privacy_Add
    fun getGroupPrivacyAdd(): Int {
        return Group_Privacy_Add
    }

    fun setGroupPrivacyAdd(groupPrivacyAdd: Int) {
        Group_Privacy_Add = groupPrivacyAdd
    }

    // Getter and setter methods for Group_Privacy_Message
    fun getGroupPrivacyMessage(): Int {
        return Group_Privacy_Message
    }

    fun setGroupPrivacyMessage(groupPrivacyMessage: Int) {
        Group_Privacy_Message = groupPrivacyMessage
    }
}
