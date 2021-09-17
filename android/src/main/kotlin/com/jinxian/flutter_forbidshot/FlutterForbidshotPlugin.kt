package com.jinxian.flutter_forbidshot

import android.content.Context
import android.media.AudioManager
import android.view.WindowManager
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** FlutterForbidshotPlugin  */
class FlutterForbidshotPlugin :FlutterPlugin, MethodCallHandler,ActivityAware {
    private var activityAware: ActivityPluginBinding?=null
    private var channel:MethodChannel?=null
    override fun onMethodCall(call: MethodCall, result: Result) {
        if (call.method.equals("setOn")) {
            activityAware?.activity?.getWindow()?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else if (call.method.equals("setOff")) {
            activityAware?.activity?.getWindow()?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else if (call.method.equals("volume")) {
            result.success(volume)
        } else if (call.method.equals("setVolume")) {
            val volume: Double = call.argument("volume")?:0f.toDouble()
            setVolume(volume)
            result.success(null)
        }
    }

    var audioManager: AudioManager? = null
    private val volume: Float
        private get() {
            val max: Float = (audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC)?:0f).toFloat()
            val current: Float =( audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)?:0f).toFloat()
            return current / max
        }

    private fun setVolume(volume: Double) {
        val max: Int = audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC)?:0
        audioManager?.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            (max * volume).toInt(),
            AudioManager.FLAG_PLAY_SOUND
        )
    }
    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(binding.binaryMessenger, "flutter_forbidshot")
        channel?.setMethodCallHandler(this)
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel?.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activityAware=binding
        audioManager =binding.activity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    override fun onDetachedFromActivity() {
        activityAware=null
    }

    override fun onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity()
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        onAttachedToActivity(binding)
    }
}