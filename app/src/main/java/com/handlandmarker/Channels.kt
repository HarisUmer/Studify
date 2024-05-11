package com.handlandmarker

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.studify.R
import com.handlandmarker.AgoraPart.Audio.AudioCallActivity
import com.handlandmarker.AgoraPart.ScreenShare.ScreenSharing_Activity
import com.handlandmarker.AgoraPart.Vedio.VedioCall_Activity
import com.handlandmarker.MainPages.textChat
import com.handlandmarker.accets.CurrentUser
import com.handlandmarker.accets.My_Group

class Channels: AppCompatActivity() {
    lateinit var Vedio : TextView
    lateinit var Audio:TextView
    lateinit var Text: TextView
    lateinit var Screen: TextView
     var Group :My_Group? = null
    fun findGroup(id: String?): My_Group? {
        for (grp in CurrentUser.Groups) {
            if (grp.getGroupID().contains(id!!)) {
                return grp
            }
        }
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val extras = intent.extras
        if (extras != null) {
            val name = extras.getString("groupName")
            val ID = extras.getString("groupID")
            Group = findGroup(ID)
        }
        setContentView(R.layout.activity_channels)
        Vedio = findViewById(R.id.vid_channel)
        Audio = findViewById(R.id.voice_channel)
        Text = findViewById(R.id.text_channel)
        Screen = findViewById(R.id.scrn_share_channel)

        Vedio.setOnClickListener {
            val intent = Intent(this, VedioCall_Activity::class.java)
            intent.putExtra("groupID", Group?.getGroupID()) // Pass the My_Group object
            startActivity(intent)
        }

        // Set OnClickListener for Audio TextView
        Audio.setOnClickListener {
            val intent = Intent(this, AudioCallActivity::class.java)
            intent.putExtra("groupID", Group?.getGroupID()) // Pass the My_Group object
            startActivity(intent)
        }

        Screen.setOnClickListener {
            //val intent = Intent(this, ScreenSharing_Activity::class.java)
            val intent = Intent(this, StartHandRecig::class.java)
            intent.putExtra("groupID", Group?.getGroupID()) // Pass the My_Group object
            startActivity(intent)
        }

        // Set OnClickListener for Audio TextView
        Text.setOnClickListener {
            val intent = Intent(this, textChat::class.java)
            intent.putExtra("groupID", Group?.getGroupID()) // Pass the My_Group object
            startActivity(intent)
        }


    }
}