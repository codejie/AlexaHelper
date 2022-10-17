package jie.android.alexahelper.smartwatchsdk

import android.content.Context
import android.util.Log

interface OnResultCallback {
    fun onResult(data: String, extra: Any?)
}

interface OnActionListener {
    fun onAction(data: String, extra: Any?, callback: OnResultCallback)
}

class SmartWatchSDK constructor() {

    private lateinit var onActionListener: OnActionListener;

    fun attach(context: Context, actionListener: OnActionListener) {
        this.onActionListener = actionListener
    }

    fun action(data: String, extra: Any?, callback: OnResultCallback) {
        this.onActionListener.onAction(data, extra, object: OnResultCallback {
            override fun onResult(data: String, extra: Any?) {
                Log.d("TAG", "$data")
            }
        })
        callback.onResult(data, extra)


    }

}