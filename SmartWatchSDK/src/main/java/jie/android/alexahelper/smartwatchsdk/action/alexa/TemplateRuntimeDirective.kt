package jie.android.alexahelper.smartwatchsdk.action.alexa

import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.channel.alexa.DirectiveParser
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.AlexaConst
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.Directive
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.*
import jie.android.alexahelper.smartwatchsdk.utils.Logger
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

private data class ImageStructure constructor(val description: String?, val sources: ArrayList<ImageSource>?) {
    data class ImageSource constructor(
        val url: String?,
        val darkUrl: String?,
        val size: String?,
        val width: Int?,
        val height: Int?
    )
}

internal fun onTemplateRuntimeDirective(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    when (directive.name) {
        AlexaConst.NAME_RENDER_TEMPLATE -> onRenderTemplate(sdk, directive, parts)
        else -> Logger.w("unsupported TemplateRuntime directive - $directive.name")
    }
}

private fun onRenderTemplate(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    when (directive.payload!!.getString("type")) {
        "BodyTemplate1" -> onTemplateBody1(sdk, directive, parts)
        "BodyTemplate2" -> onTemplateBody2(sdk, directive, parts)
        "ListTemplate1" -> onTemplateList(sdk, directive, parts)
        "WeatherTemplate" -> onTemplateWeather(sdk, directive, parts)
        else -> Logger.w("unknown template - ${directive.payload!!.getString("type")}")
    }
}

private fun onTemplateBody1(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    val dialogId = directive.header.getString("dialogRequestId")
    val token = directive.payload!!.getString("token")
    val mainTitle = directive.payload!!.getJsonObject("title")!!.getString("mainTitle")
    val subTitle = directive.payload!!.getJsonObject("title")!!.getString("subTitle")
    val icon = directive.payload!!.getJsonObject("skillIcon")?.let { getImage(it) }
    val text = directive.payload!!.getString("textField")

    val action = ActionWrapper(SDKConst.ACTION_ALEXA_TEMPLATE_CARD).apply {
        setPayload(buildJsonObject {
            put("dialogId", dialogId)
            put("token", token)
            put("mainTitle", mainTitle)
            put("subTitle", subTitle)
            makeImage(icon)?.let { put("icon", it) }
            put("text", text)
        })
    }

    sdk.toAction(action) { }
}

private fun onTemplateBody2(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {

}

private fun onTemplateList(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {

}

private fun onTemplateWeather(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {

}

private fun getImage(json: JsonObject): ImageStructure? {
    json?.let { it ->
        val ret = ImageStructure(it.getString("contentDescription"), arrayListOf())
        val srcArray = it.getJsonArray("sources")
        srcArray?.let {
            for (index in 0 until it.size) {
                val item = it[index] as JsonObject
                ret.sources!!.add(
                    ImageStructure.ImageSource(
                    item.getString("url", false),
                    item.getString("darkBackgroundUrl", false),
                    item.getString("size", false),
                    item.getInt("widthPixels", false),
                    item.getInt("heightPixels", false)
                ))
            }
        }
        return ret
    }
    return null
}

private fun makeImage(image:ImageStructure?): JsonObject? {
    image?.let {
        return buildJsonObject {
            put("description", it.description)
            it.sources?.let {
                put("sources", buildJsonArray {
                    for (item in it) {
                        add(buildJsonObject {
                            put("url", item.url)
                            put("darkUrl", item.darkUrl)
                            put("size", item.size)
                            put("width", item.width)
                            put("height", item.height)
                        })
                    }
                })
            }
        }
    }
    return null
}

