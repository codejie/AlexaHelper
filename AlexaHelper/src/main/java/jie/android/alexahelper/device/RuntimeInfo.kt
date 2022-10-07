package jie.android.alexahelper.device

import android.content.Context
import jie.android.alexahelper.Device
import jie.android.alexahelper.utils.Singleton

class RuntimeInfo constructor(context: Context) {
    var verifierCode: String? = null

    var authorizeCode: String? = null
    var redirectUri: String? = null

    var accessToken: String? = null
    var refreshToken: String? = null

    init {
        load(context)
    }

    private fun load(context: Context):Unit {

    }
    public fun flush(context: Context): Unit {

    }
}