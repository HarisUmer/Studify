package com.handlandmarker.accets;

import com.handlandmarker.accets.Users;

import java.util.ArrayList;

public class CurrentUser {
    public static Users globalVariable;
    public static ArrayList<My_Group> Groups;

    public  static ArrayList<String> AllGroupIDs = new ArrayList<>();
    public static My_Group CurrentGroup = null;

    public  static boolean  checkGroup(String Grp_ID)
    {
        for(My_Group g1: CurrentUser.Groups)
        {
            if(g1.getGroupID().contains(Grp_ID))
            {
                return false;
            }
        }
        if(AllGroupIDs.contains(Grp_ID))
        {
            return  false;
        }
        return  true;
    }

    public CurrentUser(Users f1) {
        globalVariable = f1;
    }
}
