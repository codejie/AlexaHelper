package jie.android.alexahelper.api

import java.util.*

internal fun makeMessageId(): String {
    return UUID.randomUUID().toString()
}

open class Header constructor(val namespace: String, val name: String, val messageId: String? = makeMessageId()) {
}

open class Payload {}

open class Protocol (val type: String, namespace: String, name: String, messageId: String?) {
    val header: Header = Header(namespace, name, messageId)
    var payload: Payload? = null
    var context: Any? = null

    override fun toString(): String {
        return "$type { namespace = ${header.namespace} | name = ${header.name}"
    }
}

//open class Event (namespace: String, name: String, messageId: String? = makeMessageId()):
//    Protocol("Event", namespace, name, messageId) {
//}
//
//open class Directive (namespace: String, name: String, messageId: String? = makeMessageId()):
//    Protocol("Directive", namespace, name, messageId) {
//}