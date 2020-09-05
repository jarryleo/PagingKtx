package cn.leo.paging_ktx

import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import androidx.paging.PagedList
import java.util.concurrent.Executor


/**
 * @author : ling luo
 * @date : 2020/9/5
 * @description : 数据源辅助类
 */

/**
 * 普通list转换成分页list
 */
fun <T> List<T>.toPageList(pageSize: Int = 20): PagedList<T> {
    val myConfig = PagedList.Config.Builder()
        .setInitialLoadSizeHint(pageSize)
        .setPageSize(pageSize)
        .build()
    val provider = DataProvider(this)
    val dataSource = ListDataSource(provider)
    return PagedList.Builder(dataSource, myConfig)
        .setNotifyExecutor(UiThreadExecutor())
        .setFetchExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        .setInitialKey(0)
        .build()
}

class UiThreadExecutor : Executor {
    private val handler = Handler(Looper.getMainLooper())
    override fun execute(command: Runnable) {
        handler.post(command)
    }
}