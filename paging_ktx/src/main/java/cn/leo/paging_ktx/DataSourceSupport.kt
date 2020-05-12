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
    ) -> Unit = { _, _ -> },
    range: (
        PositionalDataSource.LoadRangeParams,
        callback: PositionalDataSource.LoadRangeCallback<T>
    ) -> Unit = { _, _ -> }
): PositionalDataSource<T> {
    return object : PositionalDataSource<T>() {
        override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<T>) {
            range(params, callback)
        }

        override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<T>) {
            initial(params, callback)
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
    ) -> Unit = { _, _ -> },
    after: (
        ItemKeyedDataSource.LoadParams<Key>,
        ItemKeyedDataSource.LoadCallback<Value>
    ) -> Unit = { _, _ -> },
    before: (
        ItemKeyedDataSource.LoadParams<Key>,
        ItemKeyedDataSource.LoadCallback<Value>
    ) -> Unit = { _, _ -> },
    key: (Value) -> Key
): ItemKeyedDataSource<Key, Value> {
    return object : ItemKeyedDataSource<Key, Value>() {
        override fun loadInitial(
            params: LoadInitialParams<Key>,
            callback: LoadInitialCallback<Value>
        ) {
            initial(params, callback)
        }

        override fun loadAfter(params: LoadParams<Key>, callback: LoadCallback<Value>) {
            after(params, callback)
        }

        override fun loadBefore(params: LoadParams<Key>, callback: LoadCallback<Value>) {
            before(params, callback)
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
    ) -> Unit = { _, _ -> },
    after: (
        PageKeyedDataSource.LoadParams<Key>,
        PageKeyedDataSource.LoadCallback<Key, Value>
    ) -> Unit = { _, _ -> },
    before: (
        PageKeyedDataSource.LoadParams<Key>,
        PageKeyedDataSource.LoadCallback<Key, Value>
    ) -> Unit = { _, _ -> }
): PageKeyedDataSource<Key, Value> {
    return object : PageKeyedDataSource<Key, Value>() {
        override fun loadInitial(
            params: LoadInitialParams<Key>,
            callback: LoadInitialCallback<Key, Value>
        ) {
            initial(params, callback)
        }

        override fun loadAfter(params: LoadParams<Key>, callback: LoadCallback<Key, Value>) {
            after(params, callback)
        }

        override fun loadBefore(params: LoadParams<Key>, callback: LoadCallback<Key, Value>) {
            before(params, callback)
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



