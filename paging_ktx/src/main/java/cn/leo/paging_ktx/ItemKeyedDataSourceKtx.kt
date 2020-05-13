package cn.leo.paging_ktx

import androidx.lifecycle.MutableLiveData
import androidx.paging.ItemKeyedDataSource

/**
 * @author : ling luo
 * @date : 2020/5/13
 */
abstract class ItemKeyedDataSourceKtx<Key, Value> : ItemKeyedDataSource<Key, Value>(),
    DataSourceState {

    val dataSourceState
            by lazy { MutableLiveData<RequestDataState>() }

    private var retryFun: () -> Unit = {}


    final override fun loadInitial(
        params: LoadInitialParams<Key>,
        callback: LoadInitialCallback<Value>
    ) {
        changeState(RequestDataState.LOADING())
        changeState(loadInitialState(params, callback))
    }

    final override fun loadAfter(params: LoadParams<Key>, callback: LoadCallback<Value>) {
        changeState(RequestDataState.LOADING(true))
        val state = loadAfterState(params, callback)
        changeState(state)
        if (state == RequestDataState.FAILED(true)) {
            retryFun = { loadAfter(params, callback) }
        }
    }

    final override fun loadBefore(params: LoadParams<Key>, callback: LoadCallback<Value>) {
        changeState(RequestDataState.LOADING(true))
        val state = loadBeforeState(params, callback)
        changeState(state)
        if (state == RequestDataState.FAILED(true)) {
            retryFun = { loadBefore(params, callback) }
        }
    }

    abstract fun loadInitialState(
        params: LoadInitialParams<Key>,
        callback: LoadInitialCallback<Value>
    ): RequestDataState

    abstract fun loadAfterState(
        params: LoadParams<Key>,
        callback: LoadCallback<Value>
    ): RequestDataState

    open fun loadBeforeState(
        params: LoadParams<Key>,
        callback: LoadCallback<Value>
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