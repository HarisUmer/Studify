package com.handlandmarker.AgoraPart.Audio

import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studify.R
import com.handlandmarker.AgoraPart.AgoraManager
import com.handlandmarker.AgoraPart.App
import com.handlandmarker.AgoraPart.AppCertificate
import com.handlandmarker.AgoraPart.RtcTokenBuilder2
import com.handlandmarker.MainPages.FirebaseHelper
import com.handlandmarker.accets.CurrentUser
import com.handlandmarker.accets.Users
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import java.lang.Math.random


class AudioCallActivity : AppCompatActivity() {

    lateinit var arr:ArrayList<Users>
    var p = random()
    var localUid =  (p*10000).toInt()// UID of the local user
    protected var agoraEngine: RtcEngine? = null // The RTCEngine instance
    protected var mListener: AgoraManager.AgoraManagerListener? = null // The event handler to notify the UI of agoraEngine events
    protected val appId = App // Your App ID from Agora console
    var channelName = "Hairs"// The name of the channel to join
    var UserID = ""
    var UserName = ""
    var remoteUids = HashSet<Int>() // An object to store uids of remote users
    var isJoined = false // Status of the video call
        private set
    var isBroadcaster = true // Local user role
    var  groupID:String? = null

    lateinit var recyclerView:RecyclerView
    lateinit var Adapter: MyAdapterAudioCall
    var UsersJoined: ArrayList<String> = ArrayList()

    var fbHelper:FirebaseHelper = FirebaseHelper()
    private val mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        // Listen for a remote user joining the channel.
        override fun onUserJoined(uid: Int, elapsed: Int) {
            Log.d("info" ,"Remote user joined $UserName")

            // Save the uid of the remote user.
            remoteUids.add(uid)
        }

        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            // Set the joined status to true.
            isJoined = true
            Log.d("info" ,"Joined Channel $channel")
            // Save the uid of the local user.
            localUid = uid
            fbHelper.AddUserToGroupVoiceChat(groupID,CurrentUser.CurrentGroup.getGroupName(),fbHelper.Voice_Box)
            addNewUserToCall()

        }

        override fun onUserOffline(uid: Int, reason: Int) {
            Log.d("info" ,"Remote user offline $uid $reason")
            // Update the list of remote Uids

            // Notify the UI
            val p1 =mListener!!.onRemoteUserLeft(uid)
            remoteUids.remove(uid)
        }

        override fun onError(err: Int) {
            when (err) {
                ErrorCode.ERR_TOKEN_EXPIRED -> Log.d("info" ,"Your token has expired")
                ErrorCode.ERR_INVALID_TOKEN -> Log.d("info" ,"Your token is invalid")
                else -> Log.d("info" ,"Error code: $err")
            }
        }
    }
    protected open fun setupAgoraEngine(): Boolean {
        try {
            // Set the engine configuration
            val config = RtcEngineConfig()
            config.mContext = baseContext
            config.mAppId = appId
            // Assign an event handler to receive engine callbacks
            config.mEventHandler = mRtcEventHandler
            // Create an RtcEngine instance
            agoraEngine = RtcEngine.create(config)
        } catch (e: Exception) {
            Log.d("Message" , e.toString())
            return false
        }
        return true
    }
    val expirationTimeInSeconds = 3600

    private fun addNewUserToCall() {
        // Listen for new users joined and add them to the list
        val userJoinedListener = object : FirebaseHelper.OnUserJoinedListener {
            override fun onUserJoined(userID: String) {
                // Create a new user object and add it to the list
                //  val newUser = Users(null,userID) // Assuming you have a constructor in Users class
                UsersJoined.add(userID)
                Adapter.notifyItemInserted(UsersJoined.size - 1)
                // Notify the adapter of the changes

            }

            override fun onUserLeft(userID: ArrayList<String>?) {
                    // Update the UsersJoined list outside the loop
                if (userID != null) {
                    runOnUiThread {
                        UsersJoined.clear()
                            UsersJoined.addAll(userID)
                        Adapter.notifyDataSetChanged()
                    }
                }
            }

        }
            fbHelper.listenForUserJoined(groupID, CurrentUser.CurrentGroup.getGroupName(),fbHelper.Voice_Box,UsersJoined,userJoinedListener)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_call)
        val tokenBuilder = RtcTokenBuilder2()
        val timestamp = (System.currentTimeMillis() / 1000 + expirationTimeInSeconds).toInt()
        var Manger = AudioSettingsManager(baseContext)
        Manger.setAudioMode(AudioManager.MODE_IN_COMMUNICATION)
        Manger.setAudioOutputDevice(AudioDeviceInfo.TYPE_BUILTIN_SPEAKER)
        Manger.setAudioInputSource(AudioDeviceInfo.TYPE_BUILTIN_MIC)

        Adapter = MyAdapterAudioCall(this, UsersJoined)
        val recyclerView: RecyclerView = findViewById(R.id.Audio_IMGS)
        recyclerView.adapter = Adapter
        recyclerView.layoutManager = GridLayoutManager(baseContext,3)



        groupID = intent.getStringExtra("groupID")
        channelName = if (groupID != null) {
            // If groupID is not null, use it to set the channelName
            "Voice_Call_$groupID"
        } else {
            // If groupID is null, fallback to a default channel name
            "Voice_Call_Default"
        }


        var result = tokenBuilder.buildTokenWithUid(App, AppCertificate,channelName,localUid,RtcTokenBuilder2.Role.ROLE_PUBLISHER,timestamp,timestamp)
        joinChannel(channelName,result)
        var leaveCall:ImageView = findViewById(R.id.endAudiocall)
        leaveCall.setOnClickListener(
            View.OnClickListener {
                fbHelper.removeUserFromGroupVoiceChat(groupID,CurrentUser.CurrentGroup.getGroupName(),fbHelper.Voice_Box)
                leaveChannel()
            }
        )
        // Initialize the Agora RTC Engine with your App ID
    }

    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(true)
    }
    open fun joinChannel(channelName: String, token: String?): Int {
        // Ensure that necessary Android permissions have been granted
        this.channelName = channelName

        // Create an RTCEngine instance
        if (agoraEngine == null) setupAgoraEngine()

        val options = ChannelMediaOptions()
        // For a Video/Voice call, set the channel profile as COMMUNICATION.
        options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
        // Set the client role to broadcaster or audience
        options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER

        // Join the channel using a token.
        agoraEngine!!.joinChannel(token, channelName, localUid, options)
        return 0
    }

    fun leaveChannel() {
        if (!isJoined) {
            // Do nothing
        } else {
            // Call the `leaveChannel` method
            agoraEngine!!.leaveChannel()

            // Set the `isJoined` status to false
            isJoined = false
            finish()
            // Destroy the engine instance

        }

    }
    protected fun destroyAgoraEngine() {
        // Release the RtcEngine instance to free up resources
        RtcEngine.destroy()
        agoraEngine = null
    }

    override fun onDestroy() {
        super.onDestroy()
        agoraEngine!!.leaveChannel()

        // Set the `isJoined` status to false
        isJoined = false
        // Destroy the engine instance
        destroyAgoraEngine()
    }
}
