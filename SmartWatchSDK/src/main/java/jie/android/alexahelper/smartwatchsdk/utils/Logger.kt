package jie.android.alexahelper.smartwatchsdk.utils

import android.util.Log

object Logger {
    private const val TAG: String = "AlexaHelper"
    public fun d(msg: String): Unit {
        Log.d(TAG, msg)
    }
    public fun i(msg: String): Unit {
        Log.i(TAG, msg)
    }
    public fun w(msg: String): Unit {
        Log.w(TAG, msg)
    }
    public fun e(msg: String): Unit {
        Log.e(TAG, msg)
    }
    public fun v(msg: String): Unit {
        Log.v(TAG, "[======]$msg")
    }
}
