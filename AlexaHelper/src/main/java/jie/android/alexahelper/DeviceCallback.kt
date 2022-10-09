package jie.android.alexahelper

enum class Message constructor(val value: Int) {
    LOGIN(1)

}

interface AppDeviceCallback {
    fun onMessage(what: Int, message: Any?): Unit
}

typealias InnerDeviceCallback = (what: Message, result: Any?) -> Unit
