package ua.cn.stu.simplemvvm.views

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import ua.cn.stu.foundation.model.Result
import ua.cn.stu.foundation.views.BaseFragment
import ua.cn.stu.simplemvvm.R
import ua.cn.stu.simplemvvm.databinding.PartResultBinding

fun <T> BaseFragment.renderSimpleResult(
    root: ViewGroup,
    result: Result<T>,
    onSuccess: (T) -> Unit
) {
    val binding = PartResultBinding.bind(root)
    renderResult(
        root = root,
        result = result,
        onSuccess = { successData ->
            root.children
                .filter { it.id != R.id.resultProgressBar && it.id != R.id.resultErrorContainer }
                .forEach { it.visibility = View.VISIBLE }
            onSuccess(successData)
        },
        onError = {
            binding.resultErrorContainer.visibility = View.VISIBLE
        },
        onPending = {
            binding.resultProgressBar.visibility = View.VISIBLE
        }
    )
}
