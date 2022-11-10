package ua.cn.stu.foundation.model.tasks.dispatchers

import ua.cn.stu.foundation.model.tasks.CancelledException
import ua.cn.stu.foundation.model.tasks.Task
import ua.cn.stu.foundation.model.tasks.TaskListener
import java.util.concurrent.atomic.AtomicBoolean

class SynchronizedTask<T>(
	private val task: Task<T>
) : Task<T> {

	@Volatile
	private var cancelled = false
	private var executed = false
	private var listenerCall = AtomicBoolean(false)

	override fun await(): T {
		synchronized(this) {
			if (cancelled) throw CancelledException()
			if (executed) throw IllegalStateException("Task has been executed")
			executed = true
		}
		return task.await()
	}

	override fun enqueue(dispatcher: Dispatcher, listener: TaskListener<T>) = synchronized(this) {
		if (cancelled) return
		if (executed) throw IllegalStateException("Task has been executed")
		executed = true
		val finalListener: TaskListener<T> = { result ->
			if (listenerCall.compareAndSet(false, true)) {
				if (!cancelled) listener(result)
			}
		}
		task.enqueue(dispatcher, finalListener)
	}

	override fun cancel() = synchronized(this) {
		if (listenerCall.compareAndSet(false, true)) {
			if (cancelled) return
			cancelled = true
			task.cancel()
		}
	}
}