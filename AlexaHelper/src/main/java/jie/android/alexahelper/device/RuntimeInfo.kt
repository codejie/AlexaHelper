package jie.android.alexahelper.device

import android.content.Context
import jie.android.alexahelper.Device
import jie.android.alexahelper.utils.Singleton

internal object RuntimeInfo {
    var verifierCode: String? = null

    var authorizeCode: String? = null
    var redirectUri: String? = null

    var accessToken: String? = null
    var refreshToken: String? = null

    internal fun load(context: Context):Unit {

    }

    internal fun flush(context: Context): Unit {

    }
}