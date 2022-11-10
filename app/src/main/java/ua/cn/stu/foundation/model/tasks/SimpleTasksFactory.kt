package ua.cn.stu.foundation.model.tasks

import android.os.Handler
import android.os.Looper
import ua.cn.stu.foundation.model.ErrorResult
import ua.cn.stu.foundation.model.FinalResult
import ua.cn.stu.foundation.model.SuccessResult
import ua.cn.stu.foundation.model.tasks.dispatchers.Dispatcher

private val handler = Handler(Looper.getMainLooper())

class SimpleTasksFactory : TasksFactory {

    override fun <T> async(body: TaskBody<T>): Task<T> {
        return SimpleTask(body)
    }

    class SimpleTask<T>(
        private val body: TaskBody<T>
    ) : Task<T> {

        var thread: Thread? = null
        var cancelled = false

        override fun await(): T = body()

        override fun enqueue(dispatcher: Dispatcher, listener: TaskListener<T>) {
            thread = Thread {
                try {
                    val data = body()
                    publishResult(listener, SuccessResult(data))
                } catch (e: Exception) {
                    publishResult(listener, ErrorResult(e))
                }
            }.apply { start() }
        }

        override fun cancel() {
            cancelled = true
            thread?.interrupt()
            thread = null
        }

        private fun publishResult(listener: TaskListener<T>, result: FinalResult<T>) {
            handler.post {
                if (cancelled) return@post
                listener(result)
            }
        }
    }
}