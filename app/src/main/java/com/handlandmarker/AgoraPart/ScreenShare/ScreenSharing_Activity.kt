package com.handlandmarker.AgoraPart.ScreenShare

import android.content.Intent
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studify.R
import com.handlandmarker.AgoraPart.AgoraManager
import com.handlandmarker.AgoraPart.App
import com.handlandmarker.AgoraPart.AppCertificate
import com.handlandmarker.AgoraPart.Audio.AudioSettingsManager
import com.handlandmarker.AgoraPart.RtcTokenBuilder2
import com.handlandmarker.MainPages.FirebaseHelper
import com.handlandmarker.MainPages.FirebaseHelper.OnMemberPresentingListener
import com.handlandmarker.MainPages.MainActivity
import com.handlandmarker.StartHandRecig
import com.handlandmarker.accets.CurrentUser
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.ScreenCaptureParameters
import io.agora.rtc2.video.VideoCanvas
import org.apache.commons.io.filefilter.FalseFileFilter

class ScreenSharing_Activity : AppCompatActivity() {

    lateinit var mediaProjectionManager: MediaProjectionManager
    var p = Math.random()
    lateinit var localSurfaceView: SurfaceView
    private val capturePermissionRequestCode = 1
    lateinit var RecyclerVi: RecyclerView
    var SurfaceView_List = ArrayList<SurfaceView>()
    var localUid = (p * 10000).toInt()
    //Agora ------------------------------------------------
    protected var agoraEngine_audio: RtcEngine? = null
    protected var agoraEngine_ScreenShare: RtcEngine? = null
    protected var mListener_audio: AgoraManager.AgoraManagerListener? = null
    protected var mListener_screen: AgoraManager.AgoraManagerListener? = null

    protected var RemortUID_Presenting = -1
    //Channel_data-----------------------------------------------------------
    protected val appId = App
    var channelName = "haris"
    var remoteUids = HashSet<Int>()
    var isJoined = false
        private set
    var isBroadcaster:String? = null

    lateinit var Addapter: ScreenShareAdapter


    //Listner

    var firebaseHelper = FirebaseHelper();

    private val mRtcEventHandler_ScreenShare: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {

        override fun onUserJoined(uid: Int, elapsed: Int) {
            remoteUids.add(uid)
        }

        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            isJoined = true
            localUid = uid
            if(isBroadcaster?.contains(CurrentUser.globalVariable.getUserID()) != true) {
                setupRemoteVideo(RemortUID_Presenting)
                runOnUiThread {
                    Toast.makeText(
                        applicationContext,
                        "Joined channel $channelName with UID: $uid",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        fun onRemoteUserLeft1(remoteUid: Int, arr1: HashSet<Int>): Int {
            if (arr1.indexOf(remoteUid) != -1) {
                val p = arr1.indexOf(remoteUid)
                return p
            }
            return -1
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            runOnUiThread {
                remoteUids.remove(uid)
            }
        }

        override fun onError(err: Int) {
            when (err) {
                ErrorCode.ERR_TOKEN_EXPIRED -> Log.d("Message", "Your token has expired")
                ErrorCode.ERR_INVALID_TOKEN -> Log.d("Message", "Your token is invalid")
                else -> Log.d("Message", "Error code: $err")
            }
        }
    }

    fun startListening() {
        val tokenBuilder = RtcTokenBuilder2()
        val timestamp = (System.currentTimeMillis() / 1000 + expirationTimeInSeconds).toInt()

        firebaseHelper.listenForMemberPresenting(CurrentUser.CurrentGroup.getGroupID(), CurrentUser.CurrentGroup.getGroupName(), object : OnMemberPresentingListener {

            override fun onMemberPresenting(presentingUserID: String?, USD_REMORT: Int) {
                if (presentingUserID != null) {
                    RemortUID_Presenting = USD_REMORT
                    isBroadcaster = presentingUserID
                    if(presentingUserID.contains("NULL")) {
                        var result = tokenBuilder.buildTokenWithUid(
                            App, AppCertificate, channelName+firebaseHelper.Voice_Box, localUid,
                            RtcTokenBuilder2.Role.ROLE_PUBLISHER, timestamp, timestamp
                        )
                        StopScreenSharing()
                        setupAgoraEngineForVoice()
                        joinChannel_VoiceChat(channelName+firebaseHelper.Voice_Box, result)
                        var Fram = findViewById<FrameLayout>(R.id.Screen_Share_Frame)
                        Fram.isVisible = false

                        var RR= findViewById<RecyclerView>(R.id.Screen_Share_RR)
                        RR.isVisible = true

                    } else {

                        var result = tokenBuilder.buildTokenWithUid(
                            App, AppCertificate, channelName, localUid,
                            RtcTokenBuilder2.Role.ROLE_PUBLISHER, timestamp, timestamp
                        )
                        setupAgoraEngineForScreenShare()
                        if (CurrentUser.globalVariable.getUserID().contains(presentingUserID)) {
                            joinChannel_ScreenShare(channelName, result)
                            isBroadcaster = presentingUserID
                            var Fram = findViewById<FrameLayout>(R.id.Screen_Share_Frame)
                            Fram.isVisible = false

                            var RR= findViewById<RecyclerView>(R.id.Screen_Share_RR)
                            RR.isVisible = true

                        } else {

                            var Fram = findViewById<FrameLayout>(R.id.Screen_Share_Frame)
                            Fram.isVisible = true

                            var RR= findViewById<RecyclerView>(R.id.Screen_Share_RR)
                            RR.isVisible = false

                            joinChannel_as_Audience(channelName,result)
                            setupRemoteVideo(USD_REMORT)
                        }
                    }
                }
            }
        })
    }

    private fun StopScreenSharing() {
        if (agoraEngine_ScreenShare != null) {
            agoraEngine_ScreenShare!!.stopScreenCapture()
            agoraEngine_ScreenShare!!.leaveChannel()
            agoraEngine_ScreenShare = null
        }
    }

    private val mRtcEventHandler_Audio: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {

        override fun onUserJoined(uid: Int, elapsed: Int) {
            remoteUids.add(uid)
        }

        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            isJoined = true
            localUid = uid
            var Fb = FirebaseHelper()
            Fb.AddUserToGroupVoiceChat(CurrentUser.CurrentGroup.getGroupID(), CurrentUser.CurrentGroup.getGroupName(), Fb._ScreenShare)
        }

        fun onRemoteUserLeft1(remoteUid: Int, arr1: HashSet<Int>): Int {
            if (arr1.indexOf(remoteUid) != -1) {
                val p = arr1.indexOf(remoteUid)
                return p
            }
            return -1
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            runOnUiThread {
                remoteUids.remove(uid)
                SurfaceView_List.remove(SurfaceView_List.get(onRemoteUserLeft1(uid, remoteUids) + 1))
                Addapter.notifyItemRemoved(onRemoteUserLeft1(uid, remoteUids) + 1)
            }
        }

        override fun onError(err: Int) {
            when (err) {
                ErrorCode.ERR_TOKEN_EXPIRED -> Log.d("Message", "Your token has expired")
                ErrorCode.ERR_INVALID_TOKEN -> Log.d("Message", "Your token is invalid")
                else -> Log.d("Message", "Error code: $err")
            }
        }
    }

    private fun updateMediaPublishOptions(publishScreen: Boolean) {
        val mediaOptions = ChannelMediaOptions()
        mediaOptions.publishCameraTrack = !publishScreen
        mediaOptions.publishMicrophoneTrack = !publishScreen
        mediaOptions.publishScreenCaptureVideo = publishScreen
        mediaOptions.publishScreenCaptureAudio = publishScreen
        agoraEngine_ScreenShare!!.updateChannelMediaOptions(mediaOptions)
    }

    protected open fun setupAgoraEngineForScreenShare(): Boolean {
        try {
            val config = RtcEngineConfig()
            config.mContext = baseContext
            config.mAppId = appId
            config.mEventHandler = mRtcEventHandler_ScreenShare
            agoraEngine_ScreenShare = RtcEngine.create(config)
            agoraEngine_ScreenShare!!.enableVideo()
            agoraEngine_ScreenShare!!.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
            agoraEngine_ScreenShare!!.setClientRole(Constants.CLIENT_ROLE_BROADCASTER)

        } catch (e: Exception) {
            Log.d("error", e.toString())
            return false
        }
        return true
    }

    protected open fun setupAgoraEngineForVoice(): Boolean {
        try {
            val config = RtcEngineConfig()
            config.mContext = baseContext
            config.mAppId = appId
            config.mEventHandler = mRtcEventHandler_Audio
            agoraEngine_audio = RtcEngine.create(config)
        } catch (e: Exception) {
            Log.d("error", e.toString())
            return false
        }
        return true
    }

    val expirationTimeInSeconds = 3600
    lateinit var foregroundServiceIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaProjectionManager = getSystemService(MediaProjectionManager::class.java)
        setContentView(R.layout.screen_sharing_activity)
        foregroundServiceIntent = Intent(this, MediaProjectionService::class.java)
        startForegroundService(foregroundServiceIntent)
        RecyclerVi = findViewById(R.id.Screen_Share_RR)

        val groupID = intent.getStringExtra("groupID")
        channelName = if (groupID != null) {
            "Screen_Share_$groupID"
        } else {
            "Screen_Share_Default"
        }

        startListening()

        var Manger = AudioSettingsManager(baseContext)
        Manger.setAudioMode(AudioManager.MODE_IN_COMMUNICATION)
        Manger.setAudioOutputDevice(AudioDeviceInfo.TYPE_BUILTIN_SPEAKER)
        Manger.setAudioInputSource(AudioDeviceInfo.TYPE_BUILTIN_MIC)

        RecyclerVi.layoutManager =
            LinearLayoutManager(baseContext, LinearLayoutManager.HORIZONTAL, false)
        var p11 = ArrayList<String>()
        p11.add("Hairs")
        p11.add("Umer")
        p11.add("Yassoob")
        Addapter = ScreenShareAdapter(baseContext, p11)
        RecyclerVi.adapter = Addapter

        //Screen Share -------------------------------------------------------

        var RR: ConstraintLayout = findViewById(R.id.Screen_Share_CL)
        RR.setOnClickListener(View.OnClickListener {
            var llM : LinearLayout = findViewById(R.id.Screen_Share_optionsView)
            llM.isVisible = false
        })
        var ScreenShareButton: Button = findViewById(R.id.Share_Screen_Share)
        ScreenShareButton.setOnClickListener(View.OnClickListener {
            // Add User as Broadcaster
            var fb: FirebaseHelper = FirebaseHelper()
            fb.addUserIDBroadcast(localUid)
        })

        var leaveCall: Button = findViewById(R.id.Screen_Share_LeaveCall)
        leaveCall.setOnClickListener(
            View.OnClickListener {
                var fb: FirebaseHelper = FirebaseHelper()
                fb.RemoveUserIDBroadcast()
                leaveChannel()
                finish()
            }
        )
        var Option_Buttopn: ImageButton = findViewById(R.id.optionButton_ScreenShare)
        Option_Buttopn.setOnClickListener(View.OnClickListener {
            var llM : LinearLayout = findViewById(R.id.Screen_Share_optionsView)
            llM.isVisible = true
        })

        var CanvasButton: Button = findViewById(R.id.Screen_share_Canvas)
        CanvasButton.setOnClickListener(
            View.OnClickListener {
                val intent = Intent(this@ScreenSharing_Activity, StartHandRecig::class.java)
                startActivity(intent)

                // Move the current activity to the background
            }
        )

        var Close_ScreenShare: Button = findViewById(R.id.End_ScreenShare)
        Close_ScreenShare.setOnClickListener(View.OnClickListener {
            var fb: FirebaseHelper = FirebaseHelper()
            fb.RemoveUserIDBroadcast()
            StopScreenSharing()
        })
    }

    open fun joinChannel_VoiceChat(channelName: String, token: String?): Int {
        this.channelName = channelName

        val options = ChannelMediaOptions()
        options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
        options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER

        agoraEngine_audio!!.joinChannel(token, channelName, localUid, options)
        return 0
    }

    open fun joinChannel_as_Audience(channelName: String, token: String?): Int {
        this.channelName = channelName

        val options = ChannelMediaOptions()
        options.channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING
        options.clientRoleType = Constants.CLIENT_ROLE_AUDIENCE

        agoraEngine_ScreenShare!!.joinChannel(token, channelName, localUid, options)
        return 0
    }

    open fun joinChannel_ScreenShare(channelName: String, token: String?): Int {
        this.channelName = channelName

        val options = ChannelMediaOptions()
        options.channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING
        options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
        val scrcc = ScreenCaptureParameters()
        scrcc.captureAudio = true
        scrcc.captureVideo = true
        scrcc.videoCaptureParameters.framerate = 30
        agoraEngine_ScreenShare!!.startScreenCapture(scrcc)
        agoraEngine_ScreenShare!!.joinChannel(token, channelName, localUid, options)
        updateMediaPublishOptions(publishScreen = true)
        return 0
    }

    protected fun setupRemoteVideo(remoteUid: Int) {
        val remoteSurfaceView = SurfaceView(baseContext)
        SurfaceView_List.add(remoteSurfaceView)
        var g111 = findViewById<FrameLayout>(R.id.Screen_Share_Frame)
        g111.addView(remoteSurfaceView)

        val videoCanvas = VideoCanvas(
            remoteSurfaceView,
            VideoCanvas.RENDER_MODE_FIT, remoteUid
        )
        agoraEngine_ScreenShare!!.setupRemoteVideo(videoCanvas)
        remoteSurfaceView.visibility = View.VISIBLE
    }

    fun leaveChannel() {
        if (!isJoined) {
        } else {
            var Fb = FirebaseHelper()
            Fb.removeUserFromGroupVoiceChat(CurrentUser.CurrentGroup.getGroupID(),CurrentUser.CurrentGroup.getGroupName(),Fb._ScreenShare)
            StopScreenSharing()
            agoraEngine_audio!!.leaveChannel()
            isJoined = false
            destroyAgoraEngine()
        }
    }

    protected fun destroyAgoraEngine() {
        RtcEngine.destroy()
        agoraEngine_audio = null
        agoraEngine_ScreenShare = null
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (agoraEngine_audio != null) {
            var Fb = FirebaseHelper()
            Fb.removeUserFromGroupVoiceChat(CurrentUser.CurrentGroup.getGroupID(),CurrentUser.CurrentGroup.getGroupName(),Fb._ScreenShare)
            StopScreenSharing()
            agoraEngine_audio!!.leaveChannel()
            isJoined = false
            destroyAgoraEngine()
        }
        isJoined = false
    }
}
