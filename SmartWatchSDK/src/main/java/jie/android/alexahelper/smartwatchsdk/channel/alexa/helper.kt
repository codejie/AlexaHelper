package jie.android.alexahelper.smartwatchsdk.channel.alexa

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*

fun makePartBoundary(): String = UUID.randomUUID().toString()

fun makeCodeChallenge(code: String): String {
    val digest: MessageDigest = MessageDigest.getInstance("SHA-256")
    val hash =  digest.digest(code.toByteArray(StandardCharsets.UTF_8))
    val base64 = Base64.getUrlEncoder().withoutPadding().encode(hash)
    return base64.toString(StandardCharsets.UTF_8)
}