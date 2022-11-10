package ua.cn.stu.foundation.model.tasks

import ua.cn.stu.foundation.model.FinalResult
import ua.cn.stu.foundation.model.tasks.dispatchers.Dispatcher

typealias TaskListener<T> = (FinalResult<T>) -> Unit

class CancelledException : Exception()

interface Task<T> {

    fun await(): T

    /**
     * Listeners are called in main thread
     */
    fun enqueue(dispatcher: Dispatcher, listener: TaskListener<T>)

    fun cancel()
}