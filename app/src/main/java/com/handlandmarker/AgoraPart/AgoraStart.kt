package com.handlandmarker.AgoraPart



import android.content.Context
import android.content.Intent
import kotlin.random.Random
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.studify.R
import com.handlandmarker.AgoraPart.ScreenShare.ScreenSharing_Activity
import com.handlandmarker.MainPages.FirebaseHelper
import io.agora.rtc2.RtcEngine

public var App = "bc0e9687749e4edb8609ce3ae55206f0"
public var AppCertificate = "f9a90e5fa8834bde9b7d474ce8127f18"

fun showMessage(message: String?, Co:Context) {
        Toast.makeText(
            Co,
            message,
            Toast.LENGTH_SHORT
        ).show()

}