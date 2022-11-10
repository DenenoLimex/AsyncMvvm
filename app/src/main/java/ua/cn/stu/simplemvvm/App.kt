package ua.cn.stu.simplemvvm

import android.app.Application
import ua.cn.stu.foundation.BaseApplication
import ua.cn.stu.foundation.model.Repository
import ua.cn.stu.foundation.model.tasks.SimpleTasksFactory
import ua.cn.stu.foundation.model.tasks.ThreadUtils
import ua.cn.stu.foundation.model.tasks.dispatchers.MainThreadDispatcher
import ua.cn.stu.simplemvvm.model.colors.InMemoryColorsRepository

/**
 * Here we store instances of model layer classes.
 */
class App : Application(), BaseApplication {

	private val tasksFactory = SimpleTasksFactory()
	private val threadUtils = ThreadUtils.Default()
	private val mainThreadDispatcher = MainThreadDispatcher()

	/**
	 * Place your repositories here, now we have only 1 repository
	 */
	override val singletonScopeDependencies: List<Any> = listOf(
		tasksFactory,
		mainThreadDispatcher,
		InMemoryColorsRepository(
			tasksFactory, threadUtils
		)
	)

}