package com.handlandmarker.AgoraPart.Vedio

import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studify.R
import com.handlandmarker.AgoraPart.AgoraManager
import com.handlandmarker.AgoraPart.App
import com.handlandmarker.AgoraPart.AppCertificate
import com.handlandmarker.AgoraPart.Audio.AudioSettingsManager
import com.handlandmarker.AgoraPart.RtcTokenBuilder2
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas


class VedioCall_Activity : AppCompatActivity() {
    var p = Math.random()
    lateinit var RecyclerVi: RecyclerView
    var SurfaceView_List = ArrayList<SurfaceView>()
    var localUid =  (p*10000).toInt()// UID of the local user
    protected var agoraEngine: RtcEngine? = null // The RTCEngine instance
    protected var mListener: AgoraManager.AgoraManagerListener? = null // The event handler to notify the UI of agoraEngine events
    protected val appId =App // Your App ID from Agora console
    var channelName = "haris" // The name of the channel to join// UID of the local user
    var remoteUids = HashSet<Int>() // An object to store uids of remote users
    var isJoined = false // Status of the video call
        private set
    var isBroadcaster = true // Local user role
    lateinit var Addapter: PersonVideoAdapter


    private val mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        // Listen for a remote user joining the channel.

        override fun onUserJoined(uid: Int, elapsed: Int) {
            Log.d("Message","Remote user joined $uid")
            // Save the uid of the remote user.
            remoteUids.add(uid)
                setupRemoteVideo(uid)
        }

        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            // Set the joined status to true.
            isJoined = true
            Log.d("Message","Joined Channel $channel")
            // Save the uid of the local user.
            localUid = uid
        }

        fun onRemoteUserLeft1(remoteUid: Int, arr1: HashSet<Int>): Int
        {
            if(arr1.indexOf(remoteUid)!= -1)
            {
                val p =arr1.indexOf(remoteUid)
                return p
            }
            return -1
        }
        override fun onUserOffline(uid: Int, reason: Int) {
            runOnUiThread {
                Log.d("Message", "Remote user offline $uid $reason")
                // Update the list of remote Uids
                // Notify the UI
                mListener?.onRemoteUserLeft(uid)
                var p1 = onRemoteUserLeft1(uid, remoteUids)
                remoteUids.remove(uid)
                SurfaceView_List.remove(SurfaceView_List.get(p1 + 1))
                Addapter.notifyItemRemoved(p1 + 1)
            }
        }

        override fun onError(err: Int) {
            when (err) {
                ErrorCode.ERR_TOKEN_EXPIRED -> Log.d("Message","Your token has expired")
                ErrorCode.ERR_INVALID_TOKEN -> Log.d("Message","Your token is invalid")
                else -> Log.d("Message","Error code: $err")
            }
        }
    }

    fun localVideoSurfaceView() {
        // Create a SurfaceView object for the local video
        val localSurfaceView = SurfaceView(baseContext)
        SurfaceView_List.add(localSurfaceView)
        Addapter.notifyItemInserted(SurfaceView_List.size-1)
        localSurfaceView.visibility = View.VISIBLE
        // Call setupLocalVideo with a VideoCanvas having uid set to 0.
        agoraEngine!!.setupLocalVideo(
            VideoCanvas(
                localSurfaceView,
                VideoCanvas.RENDER_MODE_HIDDEN,
                localUid
            )
        )
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
            // By default, the video module is disabled, call enableVideo to enable it.
            agoraEngine!!.enableVideo()
        } catch (e: Exception) {
            Log.d("error" ,e.toString())
            return false
        }
        return true
    }



    val expirationTimeInSeconds = 3600

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vedio_call)
        RecyclerVi = findViewById(R.id.Vedio_Channels)
        val groupID = intent.getStringExtra("groupID")
        channelName = if (groupID != null) {
            // If groupID is not null, use it to set the channelName
            "Video_Call_$groupID"
        } else {
            // If groupID is null, fallback to a default channel name
            "Video_Call_Default"
        }
        //--------------Token Builder -------------------------------------------
        val tokenBuilder = RtcTokenBuilder2()
        val timestamp = (System.currentTimeMillis() / 1000 + expirationTimeInSeconds).toInt()
        //------------------AudioSettingManager-----------------------------------
        var Manger = AudioSettingsManager(baseContext)
        Manger.setAudioMode(AudioManager.MODE_IN_COMMUNICATION)
        Manger.setAudioOutputDevice(AudioDeviceInfo.TYPE_BUILTIN_SPEAKER)
        Manger.setAudioInputSource(AudioDeviceInfo.TYPE_BUILTIN_MIC)
        //---------------------------Build Token-----------------------------------
        var result = tokenBuilder.buildTokenWithUid(
            App, AppCertificate,channelName,localUid,
            RtcTokenBuilder2.Role.ROLE_PUBLISHER,timestamp,timestamp)
        //------------------Recycler View------------------------------------------
        RecyclerVi.layoutManager = GridLayoutManager(baseContext,3)
        Addapter = PersonVideoAdapter(baseContext,SurfaceView_List)
        RecyclerVi.adapter = Addapter
        //---------------------------Join Channel------------------------------------
        joinChannel(channelName,result)

        //LeaveCall-----------------------------------------------------------------
        var leaveCall: ImageView = findViewById(R.id.endVediocall)
        leaveCall.setOnClickListener(
            View.OnClickListener {
                leaveChannel()
                finish()
            }
        )
        // Initialize the Agora RTC Engine with your App ID
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
        // Start local preview.
        localVideoSurfaceView()

        agoraEngine!!.startPreview()

        // Join the channel using a token.
        agoraEngine!!.joinChannel(token, channelName, localUid, options)
        return 0
    }


    protected fun setupRemoteVideo(remoteUid: Int) {
        // Create a new SurfaceView
        runOnUiThread {
            val remoteSurfaceView = SurfaceView(baseContext)
            SurfaceView_List.add(remoteSurfaceView)
            Addapter.notifyItemInserted(SurfaceView_List.size-1)
            remoteSurfaceView.setZOrderMediaOverlay(true)

            // Create a VideoCanvas using the remoteSurfaceView
            val videoCanvas = VideoCanvas(
                remoteSurfaceView,
                VideoCanvas.RENDER_MODE_FIT, remoteUid
            )
            agoraEngine!!.setupRemoteVideo(videoCanvas)
            // Set the visibility
            remoteSurfaceView.visibility = View.VISIBLE
            // Notify the UI to display the video
            mListener?.onRemoteUserJoined(remoteUid, remoteSurfaceView)
        }
    }




    fun leaveChannel() {
        if (!isJoined) {
            // Do nothing
        } else {
            // Call the `leaveChannel` method
            agoraEngine!!.leaveChannel()

            // Set the `isJoined` status to false
            isJoined = false
            // Destroy the engine instance
            destroyAgoraEngine()
        }
    }

    protected fun destroyAgoraEngine() {
        // Release the RtcEngine instance to free up resources
        RtcEngine.destroy()
        agoraEngine = null
    }


    override fun onDestroy() {
        super.onDestroy()
        if(agoraEngine!= null){
        agoraEngine!!.leaveChannel()

        // Set the `isJoined` status to false
        isJoined = false
        // Destroy the engine instance
        destroyAgoraEngine()
        }
        isJoined = false
    }

}