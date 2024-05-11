package com.handlandmarker.MainPages;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studify.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.auth.User;
import com.handlandmarker.accets.CurrentUser;
import com.handlandmarker.accets.My_Group;
import com.handlandmarker.accets.Users;

import java.util.ArrayList;
import java.util.Objects;

public class Register extends AppCompatActivity implements FirebaseAuth.AuthStateListener{

    private static final int REQUEST_CODE_PERMISSIONS = 10;
    Fragment frag_login;
    Fragment frag_register;
    SharedPreferences sPref;
    FragmentManager manager;
    Button  btn_login, btn_rsignup;
    TextView tv_rlogin, tv_lsignup;
    Context context;
    boolean flag;

    SharedPreferences.Editor editor,editor1;


    TextInputEditText lemail, lpass;
    String str_email, str_pass;


    TextInputEditText fname, lname, email, pass, rpass;

    ImageView imageView;

    Button takePhoto, choosePic;
    private static final int CAMERA_REQUEST = 1888;
    private static final int PICK_IMAGE = 100;
    // Assuming you have an ImageView named mImageView

    String str_rfname, str_rlname, str_remail, str_rpass, str_rrpass;
    BitmapDrawable drawable;

    Bitmap bitmap;

    Firebase_Auth fb;
    private SignInListener signInListener;
    private SignUpListener signUpListener;
// ...

    String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.FOREGROUND_SERVICE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fb=new Firebase_Auth(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();

        tv_lsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (frag_login != null && frag_register.isHidden()) {
                    EditText email =findViewById(R.id.et_email_login);
                    String str=email.getText().toString().trim();
                    if(str!=null)
                    {
                        email=findViewById(R.id.et_email_reg);
                        email.setText(str);
                    }
                    manager.beginTransaction().show(frag_register).hide(frag_login).commit();
                }
            }
        });

        tv_rlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (frag_register != null && frag_login.isHidden()) {
                    EditText email =findViewById(R.id.et_email_reg);
                    String str=email.getText().toString().trim();
                    if(email!=null)
                    {
                        email=findViewById(R.id.et_email_login);
                        email.setText(str);
                    }
                    manager.beginTransaction().show(frag_login).hide(frag_register).commit();
                }
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_email = lemail.getText().toString().trim();
                str_pass = lpass.getText().toString().trim();

                if (!str_email.isEmpty() && !str_pass.isEmpty()) {
                    // Call Firebase authentication method for sign in
                    if(fb.onSignIn(str_email, str_pass))
                    {
                        Intent intent = new Intent(Register.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        // Authentication failed, show error message
                        Toast.makeText(Register.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Show error if email or password fields are empty
                    Toast.makeText(Register.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        choosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, PICK_IMAGE);
            }
        });

        btn_rsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_rfname = fname.getText().toString().trim();
                str_rlname = lname.getText().toString().trim();
                str_remail = email.getText().toString().trim();
                str_rpass = pass.getText().toString().trim();
                str_rrpass = rpass.getText().toString().trim();

                drawable = (BitmapDrawable) imageView.getDrawable();
                bitmap = drawable.getBitmap();

                if (!str_rfname.isEmpty() && !str_rlname.isEmpty() && !str_remail.isEmpty() && !str_rpass.isEmpty() && !str_rrpass.isEmpty()) {
                    if (str_rpass.equals(str_rrpass)) {
                        // Call Firebase authentication method for sign up
                        if (fb.onSignUp(str_rfname, str_rlname, str_remail, str_rpass, bitmap)) {
                            // Signup successful, proceed to MainActivity
                        }
                        else {
                            // Signup failed, show error message
                            Toast.makeText(Register.this, "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Show error if passwords don't match
                        Toast.makeText(Register.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Show error if any field is empty
                    Toast.makeText(Register.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void setSignInListener(SignInListener listener) {
        this.signInListener = listener;
    }

    public void setSignUpListener(SignUpListener listener) {
        this.signUpListener = listener;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    private void init() {
        // Change the color of the Navigation Bar

        if (allPermissionsGranted()) {

        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
        manager = getSupportFragmentManager();
        frag_login = manager.findFragmentById(R.id.frag_login_view);
        frag_register = manager.findFragmentById(R.id.frag_register_view);

        btn_login = findViewById(R.id.btn_login_login);
        btn_rsignup = findViewById(R.id.btn_SignUp_reg);
        tv_lsignup = findViewById(R.id.tv_register_login);
        tv_rlogin = findViewById(R.id.tv_LogIn_reg);

//login frag
        lemail=findViewById(R.id.et_email_login);
        lpass=findViewById(R.id.et_pas_login);
// signup frag
        fname=findViewById(R.id.et_firstname_reg);
        lname=findViewById(R.id.et_lastname_reg);
        email=findViewById(R.id.et_email_reg);
        pass=findViewById(R.id.et_pas_reg);
        rpass=findViewById(R.id.et_rpas_reg);
        imageView = findViewById(R.id.img_dp);
        takePhoto = findViewById(R.id.btn_img_cap);
        choosePic = findViewById(R.id.btn_img_choose);



        // Hide the register fragment initially
        manager.beginTransaction().hide(frag_register).commit();

        sPref = getSharedPreferences("LOGIN", MODE_PRIVATE);
        editor = sPref.edit();
        editor1 = sPref.edit();
        flag = sPref.getBoolean("isLogin", false);
        if(flag)
        {
            Intent intent = new Intent(Register.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST && data != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(photo);
            } else if (requestCode == PICK_IMAGE && data != null) {
                Uri selectedImage = data.getData();
                imageView.setImageURI(selectedImage);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                // Handle permissions granted
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.nav_logout)
        {
            fb.signOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser()!=null)
        {

            CurrentUser.Groups = new ArrayList<My_Group>();
            FirebaseUser va = firebaseAuth.getCurrentUser();
            CurrentUser.globalVariable = new Users(null, va.getUid());
            Intent intent = new Intent(Register.this, MainActivity.class);
            startActivity(intent);
        }
        else {
        }
    }

    // SignInListener interface
    public interface SignInListener {
        public  boolean onSignIn(String email, String password);
    }

    // SignUpListener interface
    public interface SignUpListener {
        public boolean onSignUp(String firstName, String lastName, String email, String password, Bitmap profileImage);
    }

}
