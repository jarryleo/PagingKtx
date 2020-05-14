package cn.leo.paging_ktx

import androidx.annotation.CallSuper
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.paging.PositionalDataSource

/**
 * @author : ling luo
 * @date : 2020/5/13
 */
open class PositionalDataSourceKtx<T> : PositionalDataSource<T>(), DataSourceState {

    private val dataSourceState
            by lazy { MutableLiveData<RequestDataState>() }

    private var retryFun: () -> Unit = {}

    @CallSuper
    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<T>) {
        changeState(RequestDataState.LOADING(true))
        retryFun = { loadRange(params, callback) }
    }

    @CallSuper
    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<T>) {
        changeState(RequestDataState.LOADING())
    }


    override fun changeState(state: RequestDataState) {
        dataSourceState.postValue(state)
    }

    override fun retry() {
        retryFun.invoke()
    }

    override fun refresh() {
        invalidate()
    }

    override fun observer(owner: LifecycleOwner, observer: Observer<RequestDataState>) {
        dataSourceState.observe(owner, observer)
    }
}