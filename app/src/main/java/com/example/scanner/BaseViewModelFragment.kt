package com.example.scanner

import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


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

    open fun onViewModelStartWithRequest(state: ViewModelLifecycleState.actionOnSessionState) {

    }

}
