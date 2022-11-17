package jie.android.alexahelper.smartwatchsdk.action.sdk.device

import jie.android.alexahelper.smartwatchsdk.DeviceInfo
import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.*
import kotlinx.serialization.json.JsonObject

fun setInfoAction(sdk: SmartWatchSDK, action: ActionWrapper) {
    val payload: JsonObject = action.getPayload()!!

    val product = payload.getJsonObject("product")!!
//    DeviceInfo.Product.id = product.getString("id")
//    DeviceInfo.Product.clientId = product.getString("clientId")
    DeviceInfo.productInfo.serialNumber = product.getString("serialNumber")
//    DeviceInfo.productInfo.name = product.getString("name", false)
    DeviceInfo.productInfo.friendlyName = product.getString("friendlyName", false)
    DeviceInfo.productInfo.description = product.getString("description", false)

//    val manufacturer = payload.getJsonObject("manufacturer", false)!! // !!
//    DeviceInfo.Manufacturer.name = manufacturer?.getString("name")
//    DeviceInfo.Manufacturer.model = manufacturer?.getString("model")
    DeviceInfo.productInfo.firmware = product.getString("firmware")
    DeviceInfo.productInfo.software = product.getString("software")

    DeviceInfo.endpointInfo.load()

    action.callback?.onResult(
        ResultWrapper(action.name, SDKConst.RESULT_CODE_SUCCESS).build().toString()
    )
}
