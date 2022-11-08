package ua.cn.stu.simplemvvm.views.changecolor

import androidx.lifecycle.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ua.cn.stu.foundation.model.PendingResult
import ua.cn.stu.foundation.model.SuccessResult
import ua.cn.stu.foundation.navigator.Navigator
import ua.cn.stu.foundation.uiactions.UiActions
import ua.cn.stu.foundation.views.BaseViewModel
import ua.cn.stu.foundation.views.LiveResult
import ua.cn.stu.foundation.views.MediatorLiveResult
import ua.cn.stu.foundation.views.MutableLiveResult
import ua.cn.stu.simplemvvm.R
import ua.cn.stu.simplemvvm.model.colors.ColorsRepository
import ua.cn.stu.simplemvvm.model.colors.NamedColor
import ua.cn.stu.simplemvvm.views.changecolor.ChangeColorFragment.Screen

class ChangeColorViewModel(
    screen: Screen,
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val colorsRepository: ColorsRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(), ColorsAdapter.Listener {

    // input sources
    private val _availableColors = MutableLiveResult<List<NamedColor>>(PendingResult())
    private val _currentColorId =
        savedStateHandle.getLiveData("currentColorId", screen.currentColorId)
    private val _saveInProgress = MutableLiveData(false)

    // main destination (contains merged values from _availableColors & _currentColorId)
    private val _viewState = MediatorLiveResult<ViewState>()
    val viewState: LiveResult<ViewState> = _viewState

    // side destination, also the same result can be achieved by using Transformations.map() function.
    val screenTitle: LiveData<String> = Transformations.map(viewState) { result ->
        if (result is SuccessResult) {
            val currentColor = result.data.colorsList.first { it.selected }
            uiActions.getString(R.string.change_color_screen_title, currentColor.namedColor.name)
        } else {
            uiActions.getString(R.string.change_color_screen_title_empty)
        }
    }

    init {
        viewModelScope.launch {
            delay(2000)
            _availableColors.value = SuccessResult(colorsRepository.getAvailableColors())
        }
        // initializing MediatorLiveData
        _viewState.addSource(_availableColors) { mergeSources() }
        _viewState.addSource(_currentColorId) { mergeSources() }
        _viewState.addSource(_saveInProgress) { mergeSources() }
    }

    override fun onColorChosen(namedColor: NamedColor) {
        if (_saveInProgress.value == true) return
        _currentColorId.value = namedColor.id
    }

    private var mockError = true

    fun onSavePressed() {
        viewModelScope.launch {
            if (mockError) {
                _saveInProgress.postValue(false)
                uiActions.toast(uiActions.getString(R.string.error_happened))
                mockError = false
            } else {
                _saveInProgress.postValue(true)
                delay(2000)
                val currentColorId = _currentColorId.value ?: return@launch
                val currentColor = colorsRepository.getById(currentColorId)
                colorsRepository.currentColor = currentColor
                navigator.goBack(result = currentColor)
            }
        }
    }

    fun onCancelPressed() {
        navigator.goBack()
    }

    private fun mergeSources() {
        val colors = _availableColors.value ?: return
        val currentColorId = _currentColorId.value ?: return
        val saveInProgress = _saveInProgress.value ?: return
        _viewState.value = colors.map { colorsList ->
            ViewState(
                colorsList = colorsList.map { NamedColorListItem(it, currentColorId == it.id) },
                showSaveButton = !saveInProgress,
                showCancelButton = !saveInProgress,
                showSaveProgressBar = saveInProgress
            )
        }
    }

    fun tryAgain() {
        viewModelScope.launch {
            _availableColors.postValue(PendingResult())
            delay(2000)
            _availableColors.value = SuccessResult(colorsRepository.getAvailableColors())
        }
    }

    data class ViewState(
        val colorsList: List<NamedColorListItem>,
        val showSaveButton: Boolean,
        val showCancelButton: Boolean,
        val showSaveProgressBar: Boolean
    )
}