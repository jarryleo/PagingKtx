package cn.leo.paging_ktx

/**
 * @author : ling luo
 * @date : 2020/9/5
 * @description : 自定义列表数据源
 */
class ListDataSource<T>(private val dataProvider: DataProvider<T>) :
    PageKeyedDataSourceKtx<Int, T>() {

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, T>
    ) {
        super.loadInitial(params, callback)
        callback.onResult(
            dataProvider.getList(0, params.requestedLoadSize),
            null,
            1
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {
        super.loadBefore(params, callback)
        callback.onResult(
            dataProvider.getList(params.key, params.requestedLoadSize),
            if (params.key > 1) {
                params.key - 1
            } else {
                null
            }
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {
        super.loadAfter(params, callback)
        callback.onResult(
            dataProvider.getList(params.key, params.requestedLoadSize),
            params.key + 1
        )
    }
}