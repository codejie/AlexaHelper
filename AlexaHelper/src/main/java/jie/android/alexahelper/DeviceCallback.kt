package jie.android.alexahelper

enum class Message constructor(val value: Int) {
    LOGIN(1)

}

typealias AppDeviceCallback = (what: Int, result: Any?) -> Unit
typealias InnerDeviceCallback = (what: Message, result: Any?) -> Unit
