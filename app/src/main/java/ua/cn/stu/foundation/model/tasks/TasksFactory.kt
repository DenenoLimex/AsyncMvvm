package ua.cn.stu.foundation.model.tasks

typealias TaskBody<T> = () -> T

interface TasksFactory {
	fun <T> async(body: TaskBody<T>): Task<T>
}