package jie.android.alexahelper.device

import android.content.Context
import kotlinx.serialization.json.*
import java.io.File

internal object RuntimeInfo {
    var verifierCode: String? = null

    var authorizeCode: String? = null
    var redirectUri: String? = null

    var accessToken: String? = null
    var refreshToken: String? = null

    internal fun load(context: Context):Unit {
        val file: File = File(context.codeCacheDir, "auth_info.json")
        if (file.exists()) {
            val context = file.readText()
            val json: JsonObject? = Json.parseToJsonElement(context) as JsonObject
            if (json != null) {
                authorizeCode = json["authorizeCode"]?.jsonPrimitive?.content
                redirectUri = json["redirectUri"]?.jsonPrimitive?.content
                refreshToken = json["refreshToken"]?.jsonPrimitive?.content
            }
        }
    }

    internal fun flush(context: Context): Unit {
        if (authorizeCode == null || redirectUri == null || refreshToken == null)
            return

        val json = buildJsonObject {
            put("authorizeCode", authorizeCode)
            put("redirectUri", redirectUri)
            put("refreshToken", refreshToken)
        }
        val file: File = File(context.codeCacheDir, "auth_info.json")
        file.writeText(json.toString())
    }

    internal fun reset(context: Context): Unit {
        val file: File = File(context.codeCacheDir, "auth_info.json")
        if (file.exists()) {
            file.delete()
        }
    }
}