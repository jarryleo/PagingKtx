package cn.leo.paging_ktx

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource

/**
 * @author : ling luo
 * @date : 2020/5/13
 */
abstract class PageKeyedDataSourceKtx<Key, Value> : PageKeyedDataSource<Key, Value>(),
    DataSourceState {

    val dataSourceState
            by lazy { MutableLiveData<RequestDataState>() }

    private var retryFun: () -> Unit = {}

    override fun loadInitial(
        params: LoadInitialParams<Key>,
        callback: LoadInitialCallback<Key, Value>
    ) {
        changeState(RequestDataState.LOADING())
        changeState(loadInitialState(params, callback))
    }

    override fun loadAfter(params: LoadParams<Key>, callback: LoadCallback<Key, Value>) {
        changeState(RequestDataState.LOADING(true))
        val state = loadAfterState(params, callback)
        changeState(state)
        if (state == RequestDataState.FAILED(true)) {
            retryFun = { loadAfter(params, callback) }
        }
    }

    override fun loadBefore(params: LoadParams<Key>, callback: LoadCallback<Key, Value>) {
        changeState(RequestDataState.LOADING(true))
        val state = loadBeforeState(params, callback)
        changeState(state)
        if (state == RequestDataState.FAILED(true)) {
            retryFun = { loadBefore(params, callback) }
        }
    }

    abstract fun loadInitialState(
        params: LoadInitialParams<Key>,
        callback: LoadInitialCallback<Key, Value>
    ): RequestDataState

    abstract fun loadAfterState(
        params: LoadParams<Key>,
        callback: LoadCallback<Key, Value>
    ): RequestDataState

    open fun loadBeforeState(
        params: LoadParams<Key>,
        callback: LoadCallback<Key, Value>
    ): RequestDataState {
        return RequestDataState.SUCCESS(true)
    }

    override fun changeState(state: RequestDataState) {
        dataSourceState.postValue(state)
    }

    override fun retry() {
        retryFun.invoke()
    }
}