package com.handlandmarker.MainPages;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.studify.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.handlandmarker.accets.CurrentUser;
import com.handlandmarker.accets.My_Group;

import java.util.ArrayList;

import android.Manifest;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    DrawerLayout dl;
    Toolbar tb;
    RecyclerView rv;
    public static ChatsAdapter adapter;
    ArrayList<Chat> list;
    FloatingActionButton fab_btnAdd;
    ImageView img;
    Button SignOutButton ;

// ...
    public void  Logout()
    {
        Firebase_Auth f1 = new  Firebase_Auth();
        f1.signOut();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Firebase_Auth f1 = new Firebase_Auth();
        f1.signOut();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SignOutButton = findViewById(R.id.SignOut_User);

        init();

        SignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder JoinGroup = new AlertDialog.Builder(MainActivity.this);
                JoinGroup.setTitle("Add Group");
                View v1 =  LayoutInflater.from(MainActivity.this).inflate(R.layout.add_new_group,null,false);
                JoinGroup.setView(v1);
                EditText t1 = v1.findViewById(R.id.et_grp_name);
                Button b1 = v1.findViewById(R.id.btn_add_new_grp);
                b1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String groupName = t1.getText().toString().trim();
                        if(groupName.length()>1)
                        {
                            FirebaseHelper fb = new FirebaseHelper();
                            fb.AddNewGroup(groupName, 1, 1);
                            t1.setText("");
                        }
                    }
                });
                JoinGroup.show();
            }
        });
        rv.setHasFixedSize(true);
        fab_btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder JoinGroup = new AlertDialog.Builder(MainActivity.this);
                JoinGroup.setTitle("Join Group");
                View v1 =  LayoutInflater.from(MainActivity.this).inflate(R.layout.join_group_layout,null,false);
                JoinGroup.setView(v1);
                RecyclerView rv_Groups = v1.findViewById(R.id.rv_grps_with_search);
                FirebaseHelper firebaseHelper = new FirebaseHelper();
                joinGroupAddapter a1 = new joinGroupAddapter(CurrentUser.AllGroupIDs);
                rv_Groups.setAdapter(a1);
                firebaseHelper.loadAllGroupDocumentIDs(new FirebaseHelper.OnDocumentIDsFetchedListener() {
                    @Override
                    public void onDocumentIDsFetched(ArrayList<String> documentIDs) {
                        if (documentIDs != null) {
                            // Handle the document IDs
                            a1.notifyDataSetChanged();
                        } else {
                            // Handle the error case
                            Log.e("Firebase", "Failed to fetch document IDs");
                        }
                    }
                });

                rv_Groups.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                JoinGroup.show();
            }
        });

    }
    @Override
    public void onBackPressed() {
        if (dl.isDrawerOpen(GravityCompat.START)) {
            dl.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void init()
    {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        if(CurrentUser.globalVariable!=null)
        {
            firebaseHelper.LoadGroups_For_User(CurrentUser.globalVariable.getUserID());
        }
        else
        {
            Firebase_Auth fb= new Firebase_Auth();
            if(fb.isUserSignedIn())
            {
                CurrentUser.globalVariable = fb.getCurrentUser();
                firebaseHelper.LoadGroups_For_User(CurrentUser.globalVariable.getUserID());
            }
            else
            {
                finish();
            }
        }
        img=findViewById(R.id.img_dp);
        //----------------------------------------------------------
        firebaseHelper.db_user.collection(firebaseHelper._Users).document(CurrentUser.globalVariable.getUserID())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("FireBase_Listner", "Listen failed.", error);
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            // Document exists, handle changes
                            ArrayList<String> joinedGroups = (ArrayList<String>) snapshot.get(firebaseHelper.Group_Name);
                            if (joinedGroups != null) {
                                // The joined_Groups field exists, handle changes in the array
                                for (String groupId : joinedGroups) {
                                    // Iterate over the joined groups and handle changes
                                    if(CurrentUser.Groups!=null){
                                        if(CurrentUser.Groups.contains(groupId))
                                        {
                                            Log.d("FireBase_Listner", "User joined group: " + groupId);
                                        }
                                        else
                                        {
                                            firebaseHelper.LoadGroups_For_User(CurrentUser.globalVariable.getUserID());
                                            break;
                                        }
                                    }

                                }
                            } else {
                                Log.d("FireBase_Listner", "User has not joined any groups.");
                            }
                        } else {
                            Log.d("FireBase_Listner", "Current data: null");
                        }
                    }
                });



        //----------------------------------------------------------




        rv=findViewById(R.id.rv_chats);
        fab_btnAdd=findViewById(R.id.fab_add_contact);
        dl=findViewById(R.id.drawer_layout);
        tb =findViewById(R.id.toolbar);

        setSupportActionBar(tb);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this, dl,tb,R.string.open_nav,R.string.close_nav);
        dl.addDrawerListener(toggle);
        toggle.syncState();
        if(CurrentUser.Groups==null)
            CurrentUser.Groups =new  ArrayList<My_Group>();
        adapter = new ChatsAdapter(CurrentUser.Groups,MainActivity.this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

    }

    public void logout(MenuItem item) {
        Logout();
        finish();
    }
}