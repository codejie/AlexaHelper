package jie.android.alexahelper.smartwatchsdk

internal object DeviceInfo {
    object Product {
        var id: String? = null
        var clientId: String? = null
        var serialNumber: String? = null
        var name: String? = null
        var friendlyName: String? = null
        var description: String? = null
    }
    object Manufacturer {
        var name: String? = null
        var model: String? = null
        var firmware: String? = null
        var software: String? = null
    }
}