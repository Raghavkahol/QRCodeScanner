package com.example.scanner.base

import androidx.fragment.app.Fragment
import io.reactivex.disposables.CompositeDisposable


abstract class BaseViewModelFragment : Fragment() {

    val compositeDisposable = CompositeDisposable()
    private val viewModelBinder by ViewModelActivityBindingDelegate()


    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    fun bindViewModel(viewModel: BaseViewModel) {
        viewModelBinder.bindViewModel(viewModel)
    }

    open fun actionOnSessionState(state: ViewModelLifecycleState.actionOnSessionState) {

    }

    open fun showToast(message: String) {

    }

}
