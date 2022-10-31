package jie.android.alexahelper.smartwatchsdk.utils

import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.channel.sdk.ChannelData
import kotlinx.coroutines.*

class SDKScheduler constructor(private val sdk: SmartWatchSDK) {
    enum class TimerType {
        DOWN_CHANNEL_PING,
        TOKEN_REFRESH
    }

    data class Timer(val delayMillis: Long, val repeat: Boolean = false, val param: Any? = null) {
        var id: Long = -1L
        var counter: Long  = delayMillis
        var timeout: Boolean = false
    }

    private val delayMillis: Long = 1000L
    private var timerId: Long = 0L
    private var job: Job? = null

    private val timers: MutableList<Timer> = mutableListOf()

    fun addTimer(timer: Timer): Long {
        synchronized(timers) {
            timer.id = (++timerId)
            timers.add(timer)
            return timerId
        }
    }

    fun removeTimer(id: Long) {
        synchronized(timers) {
            timers.removeIf {
                it.id == id
            }
        }
    }

    private fun removeAllTimers() {
        synchronized(timers) {
            timers.clear()
        }
    }

    fun start() {
        synchronized(timers) {
            job = CoroutineScope(Dispatchers.IO).launch {
                do {
                    timers.removeIf { it.timeout }

                    for (timer in timers) {
                        timer.counter -= delayMillis
                        if (timer.counter <= 0L) {
                            sdk.sdkChannel.send(ChannelData(ChannelData.DataType.Timer, timer))
                            if (timer.repeat) timer.counter = timer.delayMillis else timer.timeout = true
                        }
                    }

                    delay(delayMillis)
                } while (true)
            }
        }
    }

    fun stop(){
        removeAllTimers()
        job?.cancel()
    }


}