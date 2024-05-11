package com.handlandmarker.MainPages;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.handlandmarker.accets.CurrentUser;
import com.handlandmarker.accets.Users;

import java.util.concurrent.atomic.AtomicBoolean;

public class Firebase_Auth implements Register.SignInListener, Register.SignUpListener{
    private static final String TAG = "Firebase_Auth";
    private FirebaseAuth mAuth;

    private FirebaseHelper fbHelper;
    boolean b;

    public Firebase_Auth(Register r1) {
        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        fbHelper = new FirebaseHelper();

    }
    public Firebase_Auth() {
        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        fbHelper = new FirebaseHelper();
    }
    // Register user with email and password


    // Sign in with email and password
    @Override
    public boolean onSignIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password);
        if(isUserSignedIn())
        {
            FirebaseUser user = mAuth.getCurrentUser();
            if(user!= null)
            {
                CurrentUser.globalVariable = new Users(null,user.getUid());
                fbHelper.getName(user.getUid(), new FirebaseHelper.OnNameFetchedListener() {
                    @Override
                    public void onNameFetched(String name) {
                        if (name != null) {
                            // Name fetched successfully, do something with it
                            Log.d("Firebase", "Name: " + name);
                            CurrentUser.globalVariable.setUserName(name);
                        } else {
                            // Handle case where name could not be fetched
                            Log.e("Firebase", "Failed to fetch name for the given ID");
                        }
                    }
                });

                return true;
            }

        }
        return false;
    }

    @Override
    public boolean onSignUp(String firstName, String lastName, String email, String password, Bitmap profileImage) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    // Sign-up successful, add user details to Firebase database
                    fbHelper.addNewUser(user.getUid(), firstName + lastName, email);
                    CurrentUser.globalVariable = new Users(firstName + lastName, user.getUid());
                }
            } else {
                // Sign-up failed, handle the error
                Log.e(TAG, "Sign-up failed", task.getException());
                // You can display a Toast or handle the error in another way
            }
        });

        return false;
    }

    public  Users getCurrentUser()
    {
        if(mAuth.getCurrentUser()!=null)
        {
            return new Users(null,mAuth.getCurrentUser().getUid());
        }
        return null;
    }

    // Sign out the current user
    public void signOut() {
        mAuth.signOut();
        Log.d(TAG, "User signed out");
    }

    // Check if a user is currently signed in
    public boolean isUserSignedIn() {
        return mAuth.getCurrentUser() != null;
    }
}

