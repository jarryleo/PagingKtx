package cn.leo.paging_ktx

import androidx.lifecycle.MutableLiveData
import androidx.paging.PositionalDataSource

/**
 * @author : ling luo
 * @date : 2020/5/13
 */
abstract class PositionalDataSourceKtx<T> : PositionalDataSource<T>(), DataSourceState {

    val dataSourceState
            by lazy { MutableLiveData<RequestDataState>() }

    private var retryFun: () -> Unit = {}

    final override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<T>) {
        changeState(RequestDataState.LOADING(true))
        val state = loadRangeState(params, callback)
        changeState(state)
        if (state == RequestDataState.FAILED(true)) {
            retryFun = { loadRange(params, callback) }
        }
    }

    final override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<T>) {
        changeState(RequestDataState.LOADING())
        changeState(loadInitialState(params, callback))
    }

    abstract fun loadRangeState(
        params: LoadRangeParams,
        callback: LoadRangeCallback<T>
    ): RequestDataState

    abstract fun loadInitialState(
        params: LoadInitialParams,
        callback: LoadInitialCallback<T>
    ): RequestDataState


    override fun changeState(state: RequestDataState) {
        dataSourceState.postValue(state)
    }

    override fun retry() {
        retryFun.invoke()
    }
}