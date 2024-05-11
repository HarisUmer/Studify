package com.handlandmarker.MainPages;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
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
import android.view.View;
import android.widget.Button;
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
                Firebase_Auth ff1 = new Firebase_Auth();
                ff1.signOut();
                Intent intent = new Intent(MainActivity.this,Register.class);
                startActivity(intent);
                finish();
            }
        });
        rv.setHasFixedSize(true);
        fab_btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseHelper fr = new FirebaseHelper();
                fr.addUserToGroup("SV71uHeRJOwV6HhzrX9e");
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
        firebaseHelper.LoadGroups_For_User(CurrentUser.globalVariable.getUserID());
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
}