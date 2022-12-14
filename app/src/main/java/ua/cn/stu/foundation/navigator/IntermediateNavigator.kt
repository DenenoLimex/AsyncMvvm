package ua.cn.stu.foundation.navigator

import ua.cn.stu.foundation.model.dispatchers.Dispatcher
import ua.cn.stu.foundation.utils.ResourceActions
import ua.cn.stu.foundation.views.BaseScreen

/**
 * Mediator that holds nav actions in the queue if real navigator is not active.
 */
class IntermediateNavigator(
    private val dispatcher: Dispatcher
) : Navigator {

    private val targetNavigator = ResourceActions<Navigator>(dispatcher)

    override fun launch(screen: BaseScreen) = targetNavigator {
        it.launch(screen)
    }

    override fun goBack(result: Any?) = targetNavigator {
        it.goBack(result)
    }

    fun setTarget(navigator: Navigator?) {
        targetNavigator.resource = navigator
    }

    fun clear() {
        targetNavigator.clear()
    }

}