package jie.android.alexahelper.smartwatchsdk.action.alexa

import com.amazon.identity.auth.device.api.workflow.RequestContext
import jie.android.alexahelper.smartwatchsdk.sdk.ActionWrapper

object AlexaAction {
    fun login(requestContext: RequestContext, action: ActionWrapper) = loginAction(requestContext, action)
}