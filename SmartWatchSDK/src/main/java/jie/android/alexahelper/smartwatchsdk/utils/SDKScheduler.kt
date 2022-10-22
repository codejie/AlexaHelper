package jie.android.alexahelper.smartwatchsdk.utils

import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.channel.sdk.ChannelData
import kotlinx.coroutines.*

class SDKScheduler constructor(private val sdk: SmartWatchSDK) {
    data class Timer(val delayMillis: Long, val repeat: Boolean = false, val param: Any? = null) {
        var id: Long = -1L
        var counter: Long  = delayMillis
    }

    private val delayMillis: Long = 1000L
    private var timerId: Long = 0L
    private var job: Job? = null

    private val timers: MutableList<Timer> = mutableListOf()

    fun addTimer(timer: Timer): Long {
        timer.id = (++ timerId)
        timers.add(timer)
        return timerId
    }

    fun removeTimer(id: Long) {
        timers.removeIf {
            it.id == id
        }
    }

    fun removeAllTimers() {
        timers.clear()
    }

    fun start() {
        job = CoroutineScope(Dispatchers.IO).launch {
            do {
                for (timer in timers) {
                    timer.counter -= delayMillis
                    if (timer.counter <= 0L) {
                        sdk.sdkChannel.send(ChannelData(ChannelData.DataType.Timer, timer))
                        if (timer.repeat) timer.counter = timer.delayMillis else timers.remove(timer)
                    }
                }

                delay(delayMillis)
            } while (true)
        }
    }

    fun stop(){
        removeAllTimers()
        job?.cancel()
    }


}