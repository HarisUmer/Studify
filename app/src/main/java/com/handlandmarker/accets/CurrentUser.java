package com.handlandmarker.accets;

import com.handlandmarker.accets.Users;

import java.util.ArrayList;

public class CurrentUser {
    public static Users globalVariable;
    public static ArrayList<My_Group> Groups;


    public CurrentUser(Users f1) {
        globalVariable = f1;
    }
}
