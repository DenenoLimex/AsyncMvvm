package ua.cn.stu.foundation.dispatchers

interface Dispatcher {
    fun dispatch(block: () -> Unit)
}