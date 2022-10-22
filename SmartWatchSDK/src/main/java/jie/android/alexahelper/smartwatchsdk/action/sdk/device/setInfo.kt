package jie.android.alexahelper.smartwatchsdk.action.sdk.device

import jie.android.alexahelper.smartwatchsdk.DeviceInfo
import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.*
import kotlinx.serialization.json.JsonObject

fun setInfoAction(sdk: SmartWatchSDK, action: ActionWrapper) {
    val payload: JsonObject = action.getPayload()!!

    val product = payload.getJsonObject("product")!!
    DeviceInfo.Product.id = product.getString("id")
    DeviceInfo.Product.clientId = product.getString("clientId")
    DeviceInfo.Product.serialNumber = product.getString("serialNumber")
    DeviceInfo.Product.name = product.getString("name", false)
    DeviceInfo.Product.friendlyName = product.getString("friendlyName", false)
    DeviceInfo.Product.description = product.getString("description", false)

    val manufacturer = payload.getJsonObject("manufacturer", false) // !!
    DeviceInfo.Manufacturer.name = manufacturer?.getString("name")
    DeviceInfo.Manufacturer.model = manufacturer?.getString("model")
    DeviceInfo.Manufacturer.firmware = manufacturer?.getString("firmware")
    DeviceInfo.Manufacturer.software = manufacturer?.getString("software")

    action.callback?.onResult(
        ResultWrapper(action.name, SDKConst.RESULT_CODE_SUCCESS).build().toString()
    )
}
