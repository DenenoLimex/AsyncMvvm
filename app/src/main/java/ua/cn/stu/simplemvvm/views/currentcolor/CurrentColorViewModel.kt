package ua.cn.stu.simplemvvm.views.currentcolor

import ua.cn.stu.foundation.model.PendingResult
import ua.cn.stu.foundation.model.SuccessResult
import ua.cn.stu.foundation.model.takeSuccess
import ua.cn.stu.foundation.navigator.Navigator
import ua.cn.stu.foundation.uiactions.UiActions
import ua.cn.stu.foundation.views.BaseViewModel
import ua.cn.stu.foundation.views.LiveResult
import ua.cn.stu.foundation.views.MutableLiveResult
import ua.cn.stu.simplemvvm.R
import ua.cn.stu.simplemvvm.model.colors.ColorListener
import ua.cn.stu.simplemvvm.model.colors.ColorsRepository
import ua.cn.stu.simplemvvm.model.colors.NamedColor
import ua.cn.stu.simplemvvm.views.changecolor.ChangeColorFragment

class CurrentColorViewModel(
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val colorsRepository: ColorsRepository
) : BaseViewModel() {

    private val _currentColor = MutableLiveResult<NamedColor>(PendingResult())
    val currentColor: LiveResult<NamedColor> = _currentColor

    private val colorListener: ColorListener = {
        _currentColor.postValue(SuccessResult(it))
    }

    init {
        colorsRepository.addListener(colorListener)
        load()
    }

    private fun load() {
        colorsRepository.getCurrentColor().into(_currentColor)
    }

    override fun onCleared() {
        super.onCleared()
        colorsRepository.removeListener(colorListener)
    }

    override fun onResult(result: Any) {
        super.onResult(result)
        if (result is NamedColor) {
            val message = uiActions.getString(R.string.changed_color, result.name)
            uiActions.toast(message)
        }
    }

    fun changeColor() {
        val currentColor = currentColor.value.takeSuccess() ?: return
        val screen = ChangeColorFragment.Screen(currentColor.id)
        navigator.launch(screen)
    }

    fun tryAgain() {
        load()
    }
}