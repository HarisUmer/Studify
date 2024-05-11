package com.handlandmarker.AgoraPart.Audio

import android.content.Context
import android.media.AudioManager

class AudioSettingsManager(private val context: Context) {

    private val audioManager: AudioManager by lazy {
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    // Method to set audio mode (e.g., AudioManager.MODE_NORMAL, AudioManager.MODE_IN_CALL)
    fun setAudioMode(mode: Int) {
        audioManager.mode = mode
    }

    // Method to set audio output device (e.g., AudioManager.MODE_NORMAL, AudioManager.MODE_IN_CALL)
    fun setAudioOutputDevice(device: Int) {
        audioManager.isSpeakerphoneOn = false // Turn off speakerphone
        audioManager.isBluetoothScoOn = false // Turn off Bluetooth SCO
        audioManager.mode = AudioManager.MODE_IN_COMMUNICATION // Set mode to communication for better voice quality
        audioManager.setSpeakerphoneOn(false) // Make sure speakerphone is off
        audioManager.setBluetoothScoOn(false) // Make sure Bluetooth SCO is off
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION) // Set mode to communication
        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0)
        audioManager.setRouting(AudioManager.MODE_NORMAL, device, AudioManager.ROUTE_ALL) // Set output device
    }

    // Method to set audio input source (e.g., AudioManager.MODE_NORMAL, AudioManager.MODE_IN_CALL)
    fun setAudioInputSource(source: Int) {
        audioManager.isMicrophoneMute = false // Unmute microphone
        audioManager.mode = AudioManager.MODE_IN_COMMUNICATION // Set mode to communication for better voice quality
        // Set input source
    }
}
