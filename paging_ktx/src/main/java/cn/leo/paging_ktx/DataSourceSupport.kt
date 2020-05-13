package cn.leo.paging_ktx

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import androidx.paging.PageKeyedDataSource
import androidx.paging.PositionalDataSource

/**
 * @author : ling luo
 * @date : 2020/5/12
 */

/**
 * 根据页码和每页条数获取数据的数据源
 */
fun <T> positionDataSource(
    initial: (
        PositionalDataSource.LoadInitialParams,
        callback: PositionalDataSource.LoadInitialCallback<T>
    ) -> RequestDataState = { _, _ -> RequestDataState.FAILED() },
    range: (
        PositionalDataSource.LoadRangeParams,
        callback: PositionalDataSource.LoadRangeCallback<T>
    ) -> RequestDataState = { _, _ -> RequestDataState.FAILED() }
): PositionalDataSourceKtx<T> {
    return object : PositionalDataSourceKtx<T>() {
        override fun loadRangeState(
            params: LoadRangeParams,
            callback: LoadRangeCallback<T>
        ): RequestDataState {
            return range(params, callback)
        }

        override fun loadInitialState(
            params: LoadInitialParams,
            callback: LoadInitialCallback<T>
        ): RequestDataState {
            return initial(params, callback)
        }
    }
}

/**
 * 根据最后一个条目提供的key获取下一页的数据源
 */
fun <Key, Value> itemKeyedDataSource(
    initial: (
        ItemKeyedDataSource.LoadInitialParams<Key>,
        ItemKeyedDataSource.LoadInitialCallback<Value>
    ) -> RequestDataState = { _, _ -> RequestDataState.FAILED() },
    after: (
        ItemKeyedDataSource.LoadParams<Key>,
        ItemKeyedDataSource.LoadCallback<Value>
    ) -> RequestDataState = { _, _ -> RequestDataState.FAILED() },
    before: (
        ItemKeyedDataSource.LoadParams<Key>,
        ItemKeyedDataSource.LoadCallback<Value>
    ) -> RequestDataState = { _, _ -> RequestDataState.FAILED() },
    key: (Value) -> Key
): ItemKeyedDataSourceKtx<Key, Value> {
    return object : ItemKeyedDataSourceKtx<Key, Value>() {
        override fun loadInitialState(
            params: LoadInitialParams<Key>,
            callback: LoadInitialCallback<Value>
        ): RequestDataState {
            return initial(params, callback)
        }

        override fun loadAfterState(
            params: LoadParams<Key>,
            callback: LoadCallback<Value>
        ): RequestDataState {
            return after(params, callback)
        }

        override fun loadBeforeState(
            params: LoadParams<Key>,
            callback: LoadCallback<Value>
        ): RequestDataState {
            return before(params, callback)
        }

        override fun getKey(item: Value): Key {
            return key(item)
        }
    }
}

/**
 * 根据上一页提供的下一页地址获取数据的数据源
 */
fun <Key, Value> pageKeyedDataSource(
    initial: (
        PageKeyedDataSource.LoadInitialParams<Key>,
        PageKeyedDataSource.LoadInitialCallback<Key, Value>
    ) -> RequestDataState = { _, _ -> RequestDataState.FAILED() },
    after: (
        PageKeyedDataSource.LoadParams<Key>,
        PageKeyedDataSource.LoadCallback<Key, Value>
    ) -> RequestDataState = { _, _ -> RequestDataState.FAILED() },
    before: (
        PageKeyedDataSource.LoadParams<Key>,
        PageKeyedDataSource.LoadCallback<Key, Value>
    ) -> RequestDataState = { _, _ -> RequestDataState.FAILED() }
): PageKeyedDataSourceKtx<Key, Value> {
    return object : PageKeyedDataSourceKtx<Key, Value>() {
        override fun loadInitialState(
            params: LoadInitialParams<Key>,
            callback: LoadInitialCallback<Key, Value>
        ): RequestDataState {
            return initial(params, callback)
        }

        override fun loadAfterState(
            params: LoadParams<Key>,
            callback: LoadCallback<Key, Value>
        ): RequestDataState {
            return after(params, callback)
        }

        override fun loadBeforeState(
            params: LoadParams<Key>,
            callback: LoadCallback<Key, Value>
        ): RequestDataState {
            return before(params, callback)
        }
    }
}

/**
 * 增加是否有更多数据的 工厂类
 */
class DataSourceFactoryKtx<Key, Value>(
    private val create: NoMoreData.() -> DataSource<Key, Value>
) :
    DataSource.Factory<Key, Value>() {
    private val noMoreLiveData by lazy {
        MutableLiveData<Boolean>()
    }

    private val noMoreData by lazy { NoMoreData(noMoreLiveData) }

    private var currentDataSource: DataSource<Key, Value>? = null

    override fun create(): DataSource<Key, Value> {
        currentDataSource = create.invoke(noMoreData)
        return currentDataSource!!
    }

    fun noMoreDataObserver(owner: LifecycleOwner, observer: Observer<Boolean>) {
        noMoreLiveData.observe(owner, observer)
    }

    fun refresh() {
        currentDataSource?.invalidate()
        noMoreLiveData.postValue(false)
    }

    class NoMoreData(private val liveData: MutableLiveData<Boolean>) {
        fun setNoMoreData(noMoreData: Boolean) {
            liveData.postValue(noMoreData)
        }
    }
}



