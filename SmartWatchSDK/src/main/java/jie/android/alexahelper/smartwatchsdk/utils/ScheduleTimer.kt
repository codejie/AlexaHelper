package jie.android.alexahelper.smartwatchsdk.utils

import kotlinx.coroutines.*

typealias TimerAction = (param: Any?) -> Unit

object ScheduleTimer {
    data class Timer(val delayMillis: Long, val repeat: Boolean = false, val param: Any? = null, val action: TimerAction) {
        var id: Int = -1
        var counter: Long  = delayMillis
    }

    private const val delayMillis: Long = 1000L
    private var timerId: Int = 0;
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val timers: MutableList<Timer> = mutableListOf()

    fun addTimer(timer: Timer): Int {
//        scope.launch (Dispatchers.IO) {
            timer.id = (++ timerId)
            timers.add(timer)
//        }
        return timerId
    }

    fun removeTimer(id: Int) {
//        scope.launch (Dispatchers.IO) {
            timers.removeIf {
                it.id == id
            }
//        }
    }

    fun removeAllTimers() {
        timers.clear()
    }

    private fun startCoroutineTimer(action: () -> Unit) = scope.launch(Dispatchers.IO) {
        do {
            action()
            delay(delayMillis)
        } while (true)
    }

    private lateinit var job: Job

    fun start() {
        job = startCoroutineTimer() {
//            Logger.d("ScheduledTimer - tick")
            for (timer in timers) {
                timer.counter -= delayMillis
                if (timer.counter <= 0L) {
                    scope.launch (Dispatchers.Main) {
                        timer.action(timer.param)
                    }
                    if (timer.repeat) {
                        timer.counter = timer.delayMillis
                    } else {
                        timers.remove(timer)
                    }
                }
            }
        }
    }

    fun stop() {
        removeAllTimers()
        job.cancel()
    }
}