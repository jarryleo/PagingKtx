package cn.leo.paging_ktx

import androidx.annotation.CallSuper
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.paging.PageKeyedDataSource

/**
 * @author : ling luo
 * @date : 2020/5/13
 */
open class PageKeyedDataSourceKtx<Key, Value> : PageKeyedDataSource<Key, Value>(),
    DataSourceState {

    private val dataSourceState
            by lazy { MutableLiveData<RequestDataState>() }

    private var retryBefore: () -> Unit = {}
    private var retryAfter: () -> Unit = {}

    @CallSuper
    override fun loadInitial(
        params: LoadInitialParams<Key>,
        callback: LoadInitialCallback<Key, Value>
    ) {
        changeState(RequestDataState.LOADING())
    }

    @CallSuper
    override fun loadAfter(params: LoadParams<Key>, callback: LoadCallback<Key, Value>) {
        changeState(RequestDataState.LOADING(true))
        retryAfter = { loadAfter(params, callback) }
    }

    @CallSuper
    override fun loadBefore(params: LoadParams<Key>, callback: LoadCallback<Key, Value>) {
        changeState(RequestDataState.LOADING(true))
        retryBefore = { loadBefore(params, callback) }
    }

    override fun changeState(state: RequestDataState) {
        dataSourceState.postValue(state)
    }

    override fun retry() {
        retryBefore.invoke()
        retryAfter.invoke()
    }

    override fun refresh() {
        invalidate()
    }

    override fun observer(owner: LifecycleOwner, observer: Observer<RequestDataState>) {
        dataSourceState.observe(owner, observer)
    }
}