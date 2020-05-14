package cn.leo.paging_ktx

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.paging.DataSource

/**
 * @author : ling luo
 * @date : 2020/5/12
 */

class DataSourceFactoryKtx<Key, Value>(var createBlock: () -> DataSource<Key, Value>) :
    DataSource.Factory<Key, Value>(), DataSourceState {

    private val mainHandler by lazy {
        Handler(Looper.getMainLooper())
    }
    private var dataSourceState: DataSourceState? = null
    private var owner: LifecycleOwner? = null
    private var observer: Observer<RequestDataState>? = null

    override fun create(): DataSource<Key, Value> {
        val dataSource = createBlock()
        dataSourceState = dataSource as? DataSourceState
        observerOnMain()
        return dataSource
    }

    override fun changeState(state: RequestDataState) {
        dataSourceState?.changeState(state)
    }

    override fun retry() {
        dataSourceState?.retry()
    }

    override fun refresh() {
        dataSourceState?.refresh()
    }

    override fun observer(owner: LifecycleOwner, observer: Observer<RequestDataState>) {
        this.owner = owner
        this.observer = observer
        observerOnMain()
    }

    fun observerOnMain() {
        if (owner != null && observer != null) {
            mainHandler.post {
                dataSourceState?.observer(owner!!, observer!!)
            }
        }
    }

}



