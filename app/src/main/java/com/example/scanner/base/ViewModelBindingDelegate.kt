package com.example.scanner.base


import io.reactivex.disposables.Disposable
import java.lang.ref.WeakReference
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ViewModelBinder(private val fragmentRef: WeakReference<BaseViewModelFragment>) {

        fun bindViewModel(viewModel: BaseViewModel) {
            fragmentRef.get()?.compositeDisposable?.addAll(*observeAllStates(viewModel))
            viewModel.onBind()
        }

    private fun observeAllStates(viewModel: BaseViewModel): Array<Disposable> {
        val disposableList :ArrayList<Disposable> = arrayListOf()

        if(viewModel.lifecycleState.hasNoObservers())
            disposableList.add(viewModel.lifecycleState.subscribe { state ->
                handleLifecycleStateChange(state) })


        return disposableList.toTypedArray()
    }

    private fun handleLifecycleStateChange(state: ViewModelLifecycleState) {
        fragmentRef.get()?.let {
                when (state) {
                    is ViewModelLifecycleState.actionOnSessionState ->
                        it.actionOnSessionState(state)
                    is ViewModelLifecycleState.showToast ->
                        it.showToast(state.message)
                }
        }
    }

}

class ViewModelActivityBindingDelegate : ReadOnlyProperty<BaseViewModelFragment, ViewModelBinder> {
    override fun getValue(thisRef: BaseViewModelFragment, property: KProperty<*>): ViewModelBinder {
        return ViewModelBinder(WeakReference(thisRef))
    }
}
