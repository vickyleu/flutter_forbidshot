package com.jinxian.flutter_forbidshot

import android.content.Context
import android.provider.Settings
import android.view.WindowManager
import android.media.AudioManager
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar

/** FlutterForbidshotPlugin  */
class FlutterForbidshotPlugin private constructor(registrar: Registrar) : MethodCallHandler {
    private val _registrar: Registrar
    @Override
    fun onMethodCall(call: MethodCall, result: Result) {
        if (call.method.equals("setOn")) {
            _registrar.activity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else if (call.method.equals("setOff")) {
            _registrar.activity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else if (call.method.equals("volume")) {
            result.success(volume)
        } else if (call.method.equals("setVolume")) {
            val volume: Double = call.argument("volume")
            setVolume(volume)
            result.success(null)
        }
    }

    var audioManager: AudioManager? = null
    private val volume: Float
        private get() {
            if (audioManager == null) {
                audioManager =
                    _registrar.activity().getSystemService(Context.AUDIO_SERVICE) as AudioManager
            }
            val max: Float = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val current: Float = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            return current / max
        }

    private fun setVolume(volume: Double) {
        val max: Int = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            (max * volume).toInt(),
            AudioManager.FLAG_PLAY_SOUND
        )
    }

    companion object {
        /** Plugin registration.  */
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "flutter_forbidshot")
            channel.setMethodCallHandler(FlutterForbidshotPlugin(registrar))
        }
    }

    init {
        _registrar = registrar
    }
}