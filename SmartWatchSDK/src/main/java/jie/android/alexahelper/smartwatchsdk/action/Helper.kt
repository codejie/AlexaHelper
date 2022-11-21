package jie.android.alexahelper.smartwatchsdk.action

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal fun makeDate(): String {
    val current = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
    return current.format(formatter)
}
