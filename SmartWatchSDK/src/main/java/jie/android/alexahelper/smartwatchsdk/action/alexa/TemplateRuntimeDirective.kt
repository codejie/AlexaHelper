package jie.android.alexahelper.smartwatchsdk.action.alexa

import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.channel.alexa.DirectiveParser
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.AlexaConst
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.Directive
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.*
import jie.android.alexahelper.smartwatchsdk.utils.Logger
import kotlinx.serialization.json.*

internal data class ImageStructure constructor(val description: String?, val sources: ArrayList<ImageSource>?) {
    data class ImageSource constructor(
        val url: String?,
        val darkUrl: String?,
        val size: String?,
        val width: Int?,
        val height: Int?
    )
}
//internal data class ListItem constructor(val left: String?, val right: String?) {}

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
    val subTitle = directive.payload!!.getJsonObject("title", false)?.getString("subTitle", false)
    val icon = directive.payload!!.getJsonObject("skillIcon", false)?.let { getImage(it) }
    val text = directive.payload!!.getString("textField")

    val action = ActionWrapper(SDKConst.ACTION_ALEXA_TEMPLATE_CARD).apply {
        setPayload(buildJsonObject {
            put("dialogId", dialogId)
            put("token", token)
            put("mainTitle", mainTitle)
            subTitle?.let { put("subTitle", it)}
            makeImage(icon)?.let { put("icon", it) }
            put("text", text)
        })
    }

    sdk.toAction(action) { }
}

private fun onTemplateBody2(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    val dialogId = directive.header.getString("dialogRequestId")
    val token = directive.payload!!.getString("token")
    val mainTitle = directive.payload.getJsonObject("title")!!.getString("mainTitle")
    val subTitle = directive.payload.getJsonObject("title", false)?.getString("subTitle", false)
    val icon = directive.payload.getJsonObject("skillIcon", false)?.let { getImage(it) }
    val text = directive.payload.getString("textField")
    val image = directive.payload.getJsonObject("image")?.let { getImage(it) }

    val action = ActionWrapper(SDKConst.ACTION_ALEXA_TEMPLATE_CARD).apply {
        setPayload(buildJsonObject {
            put("dialogId", dialogId)
            put("token", token)
            put("mainTitle", mainTitle)
            subTitle?.let { put("subTitle", it)}
            makeImage(icon)?.let { put("icon", it) }
            put("text", text)
            makeImage(image)?.let { put("image", it) }
        })
    }

    sdk.toAction(action) { }
}

fun onTemplateList(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    val dialogId = directive.header.getString("dialogRequestId")
    val token = directive.payload!!.getString("token")
    val mainTitle = directive.payload!!.getJsonObject("title")!!.getString("mainTitle")
    val subTitle = directive.payload!!.getJsonObject("title", false)?.getString("subTitle", false)
    val icon = directive.payload!!.getJsonObject("skillIcon", false)?.let { getImage(it) }
    var items: JsonArray? = null
    directive.payload!!.getJsonArray("listItems")?.let {
        items = buildJsonArray {
            it.forEach { item ->
                this.add(buildJsonObject {
                    item.jsonObject.getString("leftTextField").let { put("left", it) }
                    item.jsonObject.getString("rightTextField").let { put("right", it) }
                })
            }
        }
    }

    val action = ActionWrapper(SDKConst.ACTION_ALEXA_TEMPLATE_LIST).apply {
        setPayload(buildJsonObject {
            put("dialogId", dialogId)
            put("token", token)
            put("mainTitle", mainTitle)
            subTitle?.let { put("subTitle", it)}
            makeImage(icon)?.let { put("icon", it) }
            items?.let { put("items", it) }
        })
    }

    sdk.toAction(action) { }
}

private fun onTemplateWeather(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    val dialogId = directive.header.getString("dialogRequestId")
    val token = directive.payload!!.getString("token")
    val mainTitle = directive.payload.getJsonObject("title")!!.getString("mainTitle")
    val subTitle = directive.payload.getJsonObject("title", false)?.getString("subTitle", false)
    val icon = directive.payload.getJsonObject("skillIcon", false)?.let { getImage(it) }
    val description = directive.payload.getString("description")
    val currentWeather = directive.payload.getString("currentWeather")
    val currentIcon = directive.payload.getJsonObject("currentWeatherIcon")?.let { getImage(it) }
    val currentCode = directive.payload.getInt("currentWeatherIconCode", false)
    val highValue = directive.payload.getJsonObject("highTemperature")?.getString("value")
    val highIcon = directive.payload.getJsonObject("highTemperature")?.getJsonObject("arrow")?.let { getImage(it) }
    val lowValue = directive.payload.getJsonObject("lowTemperature")?.getString("value")
    val lowIcon = directive.payload.getJsonObject("lowTemperature")?.getJsonObject("arrow")?.let { getImage(it) }
    var forecast: JsonArray? = null
    directive.payload!!.getJsonArray("weatherForecast")?.let {
        forecast = buildJsonArray {
            it.forEach { item ->
                this.add(buildJsonObject {
                    item.jsonObject.getInt("iconCode", false)?.let { put("code", it) }
                    item.jsonObject.getJsonObject("image")?.let { makeImage(getImage(it))?.let { it1 ->
                        put("image", it1)
                    } }
                    put("day", item.jsonObject.getString("day"))
                    put("date", item.jsonObject.getString("date"))
                    put("highValue", item.jsonObject.getString("highTemperature"))
                    put("lowValue", item.jsonObject.getString("lowTemperature"))
                })
            }
        }
    }

    val action = ActionWrapper(SDKConst.ACTION_ALEXA_TEMPLATE_WEATHER).apply {
        setPayload(buildJsonObject {
            put("dialogId", dialogId)
            put("token", token)
            put("mainTitle", mainTitle)
            subTitle?.let { put("subTitle", it)}
            makeImage(icon)?.let { put("icon", it) }
            put("description", description)
            put("current", buildJsonObject {
                put("value", currentWeather)
                makeImage(currentIcon)?.let { put("icon", it) }
                currentCode?.let { put("code", it) }
            })
            put("temperature", buildJsonObject {
                put("highValue", highValue)
                makeImage(highIcon)?.let { put("highValue", it) }
                put("lowValue", lowValue)
                makeImage(lowIcon)?.let { put("lowValue", it) }
            })
            forecast?.let { put("forecast", it) }
        })
    }

    sdk.toAction(action) { }
}

private fun getImage(json: JsonObject): ImageStructure? {
    json?.let { it ->
        val ret = ImageStructure(it.getString("contentDescription", false), arrayListOf())
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
            it.description?.let { put("description", it) }
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

